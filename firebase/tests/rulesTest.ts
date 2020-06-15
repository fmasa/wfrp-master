import * as firebase from "@firebase/testing";
import {suite, test} from "mocha-typescript"
import * as fs from "fs";
import {uuid} from "uuidv4";
import {assertFails} from "@firebase/testing";

type CollectionReference = firebase.firestore.CollectionReference;
type Firestore = firebase.firestore.Firestore;

/*
 * ============
 *    Setup
 * ============
 */
const projectId = "firestore-emulator-example";
const coverageUrl = `http://localhost:8080/emulator/v1/projects/${projectId}:ruleCoverage.html`;

const rules = fs.readFileSync(__dirname + "/../firestore.rules", "utf8");

/**
 * Creates a new app with authentication data matching the input.
 *
 * @param {object} uid the object to use for authentication (typically {uid: some-uid})
 * @return {object} the app.
 */
function authedApp(uid: string | null): Firestore {
    return firebase
        .initializeTestApp({projectId, auth: uid === null ? null : {uid}})
        .firestore();
}

/*
 * ============
 *  Test Data
 * ============
 */
const validPartyGameMasterId = "user";

const validParty = () => ({
    id: uuid(),
    name: "Emperor's Fury",
    accessCode: "123456",
    gameMasterId: validPartyGameMasterId,
    ambitions: {
        shortTerm: 'Kill monsters!',
        longTerm: 'Buy a farm',
    },
    users: [validPartyGameMasterId],
});

function validCharacter (userId: string) {
    return {
        name: "Sigmar",
        userId: userId,
        career: "God",
        socialClass: "Warrior",
        race: "HUMAN",
        stats: {
            weaponSkill: 35,
            dexterity: 20,
            initiative: 10,
            ballisticSkill: 35,
            strength: 10,
            toughness: 10,
            agility: 10,
            intelligence: 10,
            willPower: 10,
            fellowship: 10,
        },
        maxStats: {
            weaponSkill: 35,
            dexterity: 20,
            initiative: 10,
            ballisticSkill: 35,
            strength: 10,
            toughness: 10,
            agility: 10,
            intelligence: 10,
            willPower: 10,
            fellowship: 10,
        },
        points: {
            corruption: 2,
            experience: 100,
            fate: 3,
            fortune: 2,
            wounds: 10,
            maxWounds: 10,
            resilience: 10,
            resolve: 5,
            sin: 4,
        },
        ambitions: {
            shortTerm: 'Kill monsters!',
            longTerm: 'Buy a farm',
        },
        money: {
            pennies: 1000,
        }
    }
}

type Character = ReturnType<typeof validCharacter>;
type Party = ReturnType<typeof validParty>;

async function createValidParty(): Promise<Party> {
    const party = validParty();

    await authedApp(party.gameMasterId)
        .collection("parties")
        .doc(party.id)
        .set(party);

    return party
}

async function setUserInvitation(userId: string, partyId: string, accessCode: string): Promise<void> {
    await authedApp(userId)
        .collection("users")
        .doc(userId)
        .set({"invitations": [{partyId, accessCode}]})
}

function withoutField(object: object, field: string) : object {
    return Object.fromEntries(Object.entries(object).filter(([fieldName]) => fieldName === field));
}

/**
 * Returns Party
 */
async function createUserAccessibleParty(userId: string): Promise<Party> {
    const party = await createValidParty();

    await setUserInvitation(userId, party.id, party.accessCode);

    const updatedParty = {...party, users: [...party.users, userId]};

    await authedApp(userId)
        .collection("parties")
        .doc(party.id)
        .set(updatedParty);

    return updatedParty;
}

async function joinParty(party: Party, userId: string): Promise<void> {
    const app = authedApp(userId);

    await setUserInvitation(userId, party.id, party.accessCode);

    await app.collection("parties")
        .doc(party.id)
        .set({users: firebase.firestore.FieldValue.arrayUnion(userId)}, {merge: true});
}

async function createCharacter(partyId: string, userId: string): Promise<Character> {
    const document = authedApp(userId)
        .collection("parties")
        .doc(partyId)
        .collection("characters")
        .doc(userId);

    const character = validCharacter(userId);

    await document.set(character);

    return character;
}

/*
 * ============
 *  Test Cases
 * ============
 */
abstract class Suite
{
    static async before() {
        await firebase.loadFirestoreRules({projectId, rules});
    }

    async before() {
        // Clear the database between tests
        await firebase.clearFirestoreData({projectId});
    }

    static async after() {
        await Promise.all(firebase.apps().map(app => app.delete()));
        console.log(`View rule coverage information at ${coverageUrl}\n`);
    }
}

@suite
class Parties extends Suite {
    @test
    async "require users to log in before creating a party"() {
        const data = validParty();
        const party = authedApp(null)
            .collection("parties")
            .doc(data.id);

        await firebase.assertFails(party.set(data));
    }

    @test
    async "require users to give name to a new party"() {
        const data = validParty();
        const party = authedApp(data.gameMasterId)
            .collection("parties")
            .doc(data.id);

        // Empty name
        await firebase.assertFails(party.set({...data, name: ""}));

        // Whitespaces only name
        await firebase.assertFails(party.set({...data, name: "\t \r"}));

        await firebase.assertSucceeds(party.set(data))
    }

    @test
    async "should NOT let users create party with incorrect field values"() {
        const data = validParty();
        const parties = authedApp(data.gameMasterId).collection("parties");

        // ID not matching document ID
        await firebase.assertFails(parties.doc(data.id).set({id: uuid()}));

        // ID not being valid UUID
        await firebase.assertFails(parties.doc("is-this-id").set({...data, id: "is-this-id"}));

        // Empty name
        await firebase.assertFails(parties.doc(data.id).set({...data, name: ""}));

        // Name too long
        await firebase.assertFails(parties.doc(data.id).set({...data, name: "a".repeat(51)}));

        // Empty access code
        await firebase.assertFails(parties.doc(data.id).set({...data, accessCode: ""}));

        // Invalid access code type
        await firebase.assertFails(parties.doc(data.id).set({...data, accessCode: 15}));

        // Unknown field
        await firebase.assertFails(parties.doc(data.id).set({...data, otherField: 15}));

        // Invalid ambition object type
        await firebase.assertFails(parties.doc(data.id).set({...data, ambitions: "foo"}));

        // Invalid ambition field type
        await firebase.assertFails(parties.doc(data.id).set({...data, ambitions: {shortTerm: "foo", longTerm: 1}}));

        // Ambition text too long
        await firebase.assertFails(
            parties.doc(data.id)
                .set({...data, ambitions: {shortTerm: "f".repeat(410), longTerm: 1}})
        );
    }

    @test
    async "should let users create party with them being gameMaster"() {
        const data = validParty();
        const party = authedApp(data.gameMasterId)
            .collection("parties")
            .doc(data.id);

        await firebase.assertFails(party.set({...data, gameMasterId: "different-user"}));
        await firebase.assertSucceeds(party.set(data))
    }

    @test
    async "should let users create party without GM (AKA Single-player party)"() {
        const data = validParty();
        const party = authedApp(data.gameMasterId)
            .collection("parties")
            .doc(data.id);

        await firebase.assertSucceeds(party.set({...data, gameMasterId: null}));
    }

    @test
    async "should only let users create party with them having access"() {
        const data = validParty();
        const party = authedApp(validPartyGameMasterId)
            .collection("parties")
            .doc(data.id);

        await firebase.assertFails(party.set({...data, users: ["different-user"]}));
        await firebase.assertFails(party.set({...data, users: [...data.users, "different-user"]}));
        await firebase.assertSucceeds(party.set(data));
    }

    @test
    async "should NOT let users create party with any of mandatory fields missing"() {
        const parties = authedApp(validPartyGameMasterId)
            .collection("parties");

        await Promise.all(Object.keys(validParty()).map(field => {
            const party = validParty();
            const partyId = party.id;

            delete party[field];

            return firebase.assertFails(parties.doc(partyId).set(party));
        }));
    }

    @test
    async "should only let users join party with an valid invitation"() {
        const party = await createValidParty();
        const userId = "joiningUser123";
        const database = authedApp(userId);

        const joinParty = () => database.collection("parties")
            .doc(party.id)
            .set({...party, users: [...party.users, userId]});

        // Wrong access code
        await setUserInvitation(userId, party.id, "differentAccessCode");
        await firebase.assertFails(joinParty());

        // Different party ID
        await setUserInvitation(userId, uuid(), party.accessCode);
        await firebase.assertFails(joinParty());

        // Valid join
        await setUserInvitation(userId, party.id, party.accessCode);
        await firebase.assertSucceeds(joinParty());
    }

    @test
    async "should NOT let users add different users to parties"() {
        const party = await createValidParty();
        const userId = "user123";

        await setUserInvitation(userId, party.id, party.accessCode);
        await firebase.assertFails(
            authedApp("differentUserId")
                .collection("parties")
                .doc(party.id)
                .set({...party, users: [...party.users, userId]})
        );
    }

    @test
    async "should NOT let users write total garbage to his document"() {
        const userId = "user123";

        const user = authedApp(userId)
            .collection("users")
            .doc(userId);

        // Missing `invitations` field
        await firebase.assertFails(user.set({}));

        // Wrong `invitations` field type
        await firebase.assertFails(user.set({invitations: 12}));

        // Unknown field
        await firebase.assertFails(user.set({
            invitations: [],
            otherField: "what?",
        }))
    }

    @test
    async "should let users to create their character in party they have access to"() {
        const userId = "user123";
        const partyId = (await createUserAccessibleParty(userId)).id;

        const character = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        await firebase.assertSucceeds(character.set(validCharacter(userId)));
    }

    @test
    async "should NOT let users to create their character in party they DON'T have access to"() {
        const userId = "user123";
        const partyId = (await createUserAccessibleParty("another-user")).id;

        const character = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        await firebase.assertFails(character.set(validCharacter(userId)));
    }

    @test
    async "should not let users create incomplete character"() {
        const userId = "user123";
        const partyId = (await createUserAccessibleParty(userId)).id;

        const character = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        const data = validCharacter(userId);

        for (const field of Object.keys(data)) {
            const newData = {...data};

            delete newData[field];

            await firebase.assertFails(character.set(newData));
        }
    }

    @test
    async "should not let users create character with invalid data"() {
        const userId = "user123";
        const partyId = (await createUserAccessibleParty(userId)).id;

        const character = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);


        const data = validCharacter(userId);

        // Empty character name
        await firebase.assertFails(character.set({...data, name: ""}));

        // Character name too long
        await firebase.assertFails(character.set({...data, name: "a".repeat(51)}));

        // Character description too long
        await firebase.assertFails(character.set({...data, description: "a".repeat(201)}));

        // Whitespaces only name
        await firebase.assertFails(character.set({...data, name: "\t \r"}));

        // User ID not matching document key
        await firebase.assertFails(character.set({...data, userId: "foo"}));

        // Empty career
        await firebase.assertFails(character.set({...data, career: ""}));

        // Career too long
        await firebase.assertFails(character.set({...data, career: "a".repeat(51)}));

        // Whitespaces only career
        await firebase.assertFails(character.set({...data, career: "\t \r"}));

        // Empty social class
        await firebase.assertFails(character.set({...data, socialClass: ""}));

        // Social class too long
        await firebase.assertFails(character.set({...data, socialClass: "a".repeat(51)}));

        // Whitespaces only social class
        await firebase.assertFails(character.set({...data, socialClass: "\t \r"}));

        // Invalid race
        await firebase.assertFails(character.set({...data, race: "ORC"}));

        // Negative amount of money
        await firebase.assertFails(character.set({...data, money: {pennies: -1}}));

        // Invalid money
        await firebase.assertFails(character.set({...data, money: {}}));

        // Extra field
        await firebase.assertFails(character.set({...data, extraField: "foo"}));

        // Invalid ambition object type
        await firebase.assertFails(character.set({...data, ambitions: "foo"}));

        // Invalid ambition field type
        await firebase.assertFails(character.set({...data, ambitions: {shortTerm: "foo", longTerm: 1}}));
    }

    @test
    async "should not let users create character with invalid stats"() {
        const userId = "user123";
        const partyId = (await createUserAccessibleParty(userId)).id;

        const character = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        const data = validCharacter(userId);

        for (const stat in Object.keys(data.stats)) {
            const newData = {...data, stats: withoutField(data.stats, stat)};

            // Missing stat
            await firebase.assertFails(character.set(newData));


            // Negative stat
            newData.stats[stat] = -1;
            await firebase.assertFails(character.set(newData));

            // Current stat larger than max value
            newData.stats[stat] = newData.maxStats[stat] + 1
            await firebase.assertFails(character.set(newData));
        }
    }

    @test
    async "should not let users create character with invalid points"() {
        const userId = "user123";
        const partyId = (await createUserAccessibleParty(userId)).id;

        const character = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        const data = validCharacter(userId);

        await firebase.assertFails(character.set({...data, stats: {...data.stats, extraStat: 10}}));

        const withPoints = (point: string, value: number) => ({...data, points: {...data.points, [point]: value}});

        for (const point in data.points) {
            // Negative point
            await firebase.assertFails(character.set(withPoints(point, -1)));
        }

        await firebase.assertSucceeds(character.set(withPoints('fortune', data.points.fate - 1)));
        await firebase.assertSucceeds(character.set(withPoints('fortune', data.points.fate)));
        await firebase.assertFails(character.set(withPoints('fortune', data.points.fate + 1)));

        await firebase.assertSucceeds(character.set(withPoints('resolve', data.points.resilience - 1)));
        await firebase.assertSucceeds(character.set(withPoints('resolve', data.points.resilience)));
        await firebase.assertFails(character.set(withPoints('resolve', data.points.resilience + 1)));

        await firebase.assertSucceeds(character.set(withPoints('wounds', data.points.maxWounds - 1)));
        await firebase.assertSucceeds(character.set(withPoints('wounds', data.points.maxWounds)));
        await firebase.assertFails(character.set(withPoints('wounds', data.points.maxWounds + 1)));

        await firebase.assertSucceeds(character.set(withPoints('experience', 1)));
        await firebase.assertSucceeds(character.set(withPoints('experience', 0)));
        await firebase.assertFails(character.set(withPoints('experience', -1)));
    }

    @test
    async "should let users update their character"() {
        const userId = "user123";
        const partyId = (await createUserAccessibleParty(userId)).id;
        await createCharacter(partyId, userId);

        const document = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        await firebase.assertSucceeds(document.set({points: {resilience: 10}}, {merge: true}));
    }

    @test
    async "should let game master update character"() {
        const userId = "user123";
        const party = await createUserAccessibleParty(userId);
        await createCharacter(party.id, userId);

        const document = authedApp(party.gameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection("characters")
            .doc(userId);

        await firebase.assertSucceeds(document.set({points: {resilience: 10}}, {merge: true}));
    }

    @test
    async "should NOT let users update other users character"() {
        const userId = "user123";
        const otherUserId = "user345";

        const party = await createUserAccessibleParty(userId);
        await createCharacter(party.id, userId);
        await joinParty(party, otherUserId);

        const document = authedApp(otherUserId)
            .collection("parties")
            .doc(party.id)
            .collection("characters")
            .doc(userId);

        await firebase.assertFails(document.set({points: {resilience: 10}}, {merge: true}));
    }

    @test
    async "should let GM edit ambitions"() {
        const party = await createValidParty();

        const document = authedApp(party.gameMasterId)
            .collection("parties")
            .doc(party.id);

        await firebase.assertSucceeds(
            document.update("ambitions", {shortTerm: "Completely new ambitions!", longTerm: "We don't think that far"})
        )
    }
}


abstract class CharacterSubCollectionSuite extends Suite {
    private _partyId: string
    private _gameMasterId: string;
    protected readonly userId1 = 'user123';
    protected readonly userId2 = 'user345';

    protected get partyId(): string {
        return this._partyId;
    }

    protected get gameMasterId(): string {
        return this._gameMasterId;
    }

    async before() {
        await super.before();

        const party = await createValidParty();
        this._partyId = party.id;
        this._gameMasterId = party.gameMasterId;

        await joinParty(party, this.userId1);
        await joinParty(party, this.userId2);

        await createCharacter(party.id, this.userId1);
        await createCharacter(party.id, this.userId2);
    }
}

@suite
class Inventory extends CharacterSubCollectionSuite {
    private inventoryItem = {
        id: uuid(),
        quantity: 1,
        name: "Sword of Chaos Champion",
        description: "Trust me, you don't want to show it to people",
    };

    private inventoryItems(app: Firestore, userId: string): CollectionReference
    {
        return app.collection("parties")
            .doc(this.partyId)
            .collection("characters")
            .doc(userId)
            .collection("inventory");
    }

    @test
    async "user (and GM) can add item to his inventory"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const items = this.inventoryItems(authedApp(userId), this.userId1);

            await firebase.assertSucceeds(items.doc(this.inventoryItem.id).set(this.inventoryItem));
        }
    }

    @test
    async "other users CANNOT add item to character's inventory"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const items = this.inventoryItems(authedApp(userId), this.userId1);

            await firebase.assertFails(items.doc(this.inventoryItem.id).set(this.inventoryItem));
        }
    }

    @test
    async "user (and GM) can update item in his inventory"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const items = this.inventoryItems(authedApp(userId), this.userId1);
            await items.doc(this.inventoryItem.id).set(this.inventoryItem);

            await firebase.assertSucceeds(items.doc(this.inventoryItem.id).set({quantity: 100}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update item in character's inventory"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.inventoryItems(authedApp(this.userId1), this.userId1).doc(this.inventoryItem.id).set(this.inventoryItem);
            const items = this.inventoryItems(authedApp(userId), this.userId1);

            await firebase.assertFails(items.doc(this.inventoryItem.id).set({quantity: 100}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove item from his inventory"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const items = this.inventoryItems(authedApp(userId), this.userId1);
            await items.doc(this.inventoryItem.id).set(this.inventoryItem);

            await firebase.assertSucceeds(items.doc(this.inventoryItem.id).delete());
        }
    }

    @test
    async "other users CANNOT remove item from character's inventory"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.inventoryItems(authedApp(this.userId1), this.userId1).doc(this.inventoryItem.id).set(this.inventoryItem);
            const items = this.inventoryItems(authedApp(userId), this.userId1);

            await firebase.assertFails(items.doc(this.inventoryItem.id).delete());
        }
    }

    @test
    async "all party members can read each other's inventory"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.inventoryItems(authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "inventory item with field missing CANNOT be saved"() {
        const items = this.inventoryItems(authedApp(this.userId1), this.userId1);

        await Promise.all(Object.keys(this.inventoryItem).map(field => {
            const item = {...this.inventoryItem};
            const itemId = item.id;

            delete item[field];

            return firebase.assertFails(items.doc(itemId).set(item));
        }));
    }

    @test
    async "item with invalid field CANNOT be saved"() {
        const itemDoc = this.inventoryItems(authedApp(this.userId1), this.userId1).doc(this.inventoryItem.id);

        await Promise.all(
            [
                // ID not matching document ID
                {id: uuid()},

                // ID is not valid UUID
                {id: "foo"},

                // Empty name
                {name: ""},

                // Name too long
                {name: "a".repeat(51)},

                // Name with nothing but whitespaces
                {name: " \t\r"},

                // Description too long
                {description: "a".repeat(201)},

                // Zero quantity
                {quantity: 0},

                // Negative quantity
                {quantity: -1}
            ].map(doc => firebase.assertFails(itemDoc.set({...this.inventoryItem, ...doc})))
        );

        await Promise.all(Object.keys(this.inventoryItem).map(field => {
            return firebase.assertFails(
                itemDoc.set({
                    ...this.inventoryItem,
                    [field]: typeof this.inventoryItem[field] == 'string' ? true : 'foo',
                })
            );
        }));
    }
}

@suite
class Skills extends CharacterSubCollectionSuite {
    private skill = {
        id: uuid(),
        advanced: false,
        characteristic: "FELLOWSHIP",
        name: "Haggle",
        description: "Lower the price of goods",
        advances: 1,
    };

    private skills(app: Firestore, userId: string): CollectionReference
    {
        return app.collection("parties")
            .doc(this.partyId)
            .collection("characters")
            .doc(userId)
            .collection("skills");
    }

    @test
    async "user (and GM) can add skill to his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const skills = this.skills(authedApp(userId), this.userId1);

            await firebase.assertSucceeds(skills.doc(this.skill.id).set(this.skill));
        }
    }

    @test
    async "other users CANNOT add skill to character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const skills = this.skills(authedApp(userId), this.userId1);

            await firebase.assertFails(skills.doc(this.skill.id).set(this.skill));
        }
    }

    @test
    async "user (and GM) can update skills of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const skills = this.skills(authedApp(userId), this.userId1);
            await skills.doc(this.skill.id).set(this.skill);

            await firebase.assertSucceeds(skills.doc(this.skill.id).set({advanced: true}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update skill of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.skills(authedApp(this.userId1), this.userId1).doc(this.skill.id).set(this.skill);
            const skills = this.skills(authedApp(userId), this.userId1);

            await firebase.assertFails(skills.doc(this.skill.id).set({advanced: true}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove skill of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const skills = this.skills(authedApp(userId), this.userId1);
            await skills.doc(this.skill.id).set(this.skill);

            await firebase.assertSucceeds(skills.doc(this.skill.id).delete());
        }
    }

    @test
    async "other users CANNOT remove skill of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.skills(authedApp(this.userId1), this.userId1).doc(this.skill.id).set(this.skill);
            const skills = this.skills(authedApp(userId), this.userId1);

            await firebase.assertFails(skills.doc(this.skill.id).delete());
        }
    }

    @test
    async "all party members can read skills"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.skills(authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "skill with field missing CANNOT be saved"() {
        const skills = this.skills(authedApp(this.userId1), this.userId1);

        await Promise.all(Object.keys(this.skill).map(field => {
            const skill = {...this.skill};
            const skillId = skill.id;

            delete skill[field];

            return firebase.assertFails(skills.doc(skillId).set(skill));
        }));
    }

    @test
    async "skill with invalid field CANNOT be saved"() {
        const skillDoc = this.skills(authedApp(this.userId1), this.userId1).doc(this.skill.id);

        await Promise.all(
            [
                // ID not matching document ID
                {id: uuid()},

                // ID is not valid UUID
                {id: "foo"},

                // Empty name
                {name: ""},

                // Name too long
                {name: "a".repeat(51)},

                // Unknown characteristic
                {characteristic: "NICENCESS"},

                // Negative advances
                {mastery: 0},

                // Description too long
                {description: "a".repeat(201)},

            ].map(doc => firebase.assertFails(skillDoc.set({...this.skill, ...doc})))
        );

        await Promise.all(Object.keys(this.skill).map(field => {
            return firebase.assertFails(
                skillDoc.set({
                    ...this.skill,
                    [field]: typeof this.skill[field] == 'string' ? true : 'foo',
                })
            );
        }));
    }
}


@suite
class Talents extends CharacterSubCollectionSuite {
    private talent = {
        id: uuid(),
        name: "Sneaky brieky",
        description: "+ 1O to sneak rolls",
        taken: 5,
    };

    private talents(app: Firestore, userId: string): CollectionReference
    {
        return app.collection("parties")
            .doc(this.partyId)
            .collection("characters")
            .doc(userId)
            .collection("talents");
    }

    @test
    async "user (and GM) can add talent to his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const talents = this.talents(authedApp(userId), this.userId1);

            await firebase.assertSucceeds(talents.doc(this.talent.id).set(this.talent));
        }
    }

    @test
    async "other users CANNOT add talent to character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const talents = this.talents(authedApp(userId), this.userId1);

            await firebase.assertFails(talents.doc(this.talent.id).set(this.talent));
        }
    }

    @test
    async "user (and GM) can update talent of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const talents = this.talents(authedApp(userId), this.userId1);
            await talents.doc(this.talent.id).set(this.talent);

            await firebase.assertSucceeds(talents.doc(this.talent.id).set({description: "Better desc."}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update talent of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.talents(authedApp(this.userId1), this.userId1).doc(this.talent.id).set(this.talent);
            const talents = this.talents(authedApp(userId), this.userId1);

            await firebase.assertFails(talents.doc(this.talent.id).set({advanced: true}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove talent of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const talents = this.talents(authedApp(userId), this.userId1);
            await talents.doc(this.talent.id).set(this.talent);

            await firebase.assertSucceeds(talents.doc(this.talent.id).delete());
        }
    }

    @test
    async "other users CANNOT remove talent of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.talents(authedApp(this.userId1), this.userId1).doc(this.talent.id).set(this.talent);
            const talents = this.talents(authedApp(userId), this.userId1);

            await firebase.assertFails(talents.doc(this.talent.id).delete());
        }
    }

    @test
    async "all party members can read talents"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.talents(authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "skill with field missing CANNOT be saved"() {
        const skills = this.talents(authedApp(this.userId1), this.userId1);

        await Promise.all(Object.keys(this.talent).map(field => {
            const talent = {...this.talent};
            const talentId = talent.id;

            delete talent[field];

            return firebase.assertFails(skills.doc(talentId).set(talent));
        }));
    }

    @test
    async "talent with invalid field CANNOT be saved"() {
        const talentDoc = this.talents(authedApp(this.userId1), this.userId1).doc(this.talent.id);

        await Promise.all(
            [
                // ID not matching document ID
                {id: uuid()},

                // ID is not valid UUID
                {id: "foo"},

                // Empty name
                {name: ""},

                // Name too long
                {name: "a".repeat(51)},

                // Description too long
                {description: "a".repeat(201)},

                // Taken too many times
                {taken: 1000},

                // Taken too few times
                {taken: 0},
            ].map(doc => firebase.assertFails(talentDoc.set({...this.talent, ...doc})))
        );

        await Promise.all(Object.keys(this.talent).map(field => {
            return firebase.assertFails(
                talentDoc.set({
                    ...this.talent,
                    [field]: typeof this.talent[field] == 'string' ? true : 'foo',
                })
            );
        }));
    }
}


@suite
class Spells extends CharacterSubCollectionSuite {
    private spell = {
        id: uuid(),
        name: "Blink",
        range: "1 yard",
        target: "1",
        duration: "3 seconds",
        castingNumber: 0,
        effect: "Your staff blinks with bright white light which blinds target for 1d10 minutes"
    };

    private spells(app: Firestore, userId: string): CollectionReference
    {
        return app.collection("parties")
            .doc(this.partyId)
            .collection("characters")
            .doc(userId)
            .collection("spells");
    }

    @test
    async "user (and GM) can add spell to his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const spells = this.spells(authedApp(userId), this.userId1);

            await firebase.assertSucceeds(spells.doc(this.spell.id).set(this.spell));
        }
    }

    @test
    async "other users CANNOT add spell to character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const spells = this.spells(authedApp(userId), this.userId1);

            await firebase.assertFails(spells.doc(this.spell.id).set(this.spell));
        }
    }

    @test
    async "user (and GM) can update spell of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const spells = this.spells(authedApp(userId), this.userId1);
            await spells.doc(this.spell.id).set(this.spell);

            await firebase.assertSucceeds(spells.doc(this.spell.id).set({effect: "Improved effect."}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update spell of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.spells(authedApp(this.userId1), this.userId1).doc(this.spell.id).set(this.spell);
            const talents = this.spells(authedApp(userId), this.userId1);

            await firebase.assertFails(talents.doc(this.spell.id).set({effect: "Improved effect."}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove spell of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const spells = this.spells(authedApp(userId), this.userId1);
            await spells.doc(this.spell.id).set(this.spell);

            await firebase.assertSucceeds(spells.doc(this.spell.id).delete());
        }
    }

    @test
    async "other users CANNOT remove spell of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.spells(authedApp(this.userId1), this.userId1).doc(this.spell.id).set(this.spell);
            const spells = this.spells(authedApp(userId), this.userId1);

            await firebase.assertFails(spells.doc(this.spell.id).delete());
        }
    }

    @test
    async "all party members can read spells"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.spells(authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "spell with field missing CANNOT be saved"() {
        const spells = this.spells(authedApp(this.userId1), this.userId1);

        await Promise.all(Object.keys(this.spell).map(field => {
            const spell = {...this.spell};
            const spellId = spell.id;

            delete spell[field];

            return firebase.assertFails(spells.doc(spellId).set(spell));
        }));
    }

    @test
    async "spell with invalid field CANNOT be saved"() {
        const spellDoc = this.spells(authedApp(this.userId1), this.userId1).doc(this.spell.id);

        await Promise.all(
            [
                // ID not matching document ID
                {id: uuid()},

                // ID is not valid UUID
                {id: "foo"},

                // Wrong casting number type
                {castingNumber: "2"},

                // Casting number too small
                {castingNumber: -1},

                // Casting number too large
                {castingNumber: 100},

                // Name too long
                {name: "a".repeat(51)},

                // Effect too long
                {effect: "a".repeat(401)},

                // Empty name
                {name: ""},

                // Whitespaces only name
                {name: "\t \r"},

                // Duration too long
                {duration: "a".repeat(51)},

                // Range too long
                {range: "a".repeat(51)},

                // target too long
                {target: "a".repeat(51)},
            ].map(doc => firebase.assertFails(spellDoc.set({...this.spell, ...doc})))
        );

        await Promise.all(Object.keys(this.spell).map(field => {
            return firebase.assertFails(
                spellDoc.set({
                    ...this.spell,
                    [field]: typeof this.spell[field] == 'string' ? true : 'foo',
                })
            );
        }));
    }
}
