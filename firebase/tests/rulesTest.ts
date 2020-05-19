import * as firebase from "@firebase/testing";
import {suite, test} from "mocha-typescript"
import * as fs from "fs";
import {uuid} from "uuidv4";

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
function authedApp(uid: string | null): firebase.firestore.Firestore {
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
    users: [validPartyGameMasterId],
});

function validCharacter (userId: string) {
    return {
        name: "Sigmar",
        userId: userId,
        career: "God",
        race: "HUMAN",
        stats: {
            weaponSkill: 35,
            ballisticSkill: 35,
            strength: 10,
            toughness: 10,
            agility: 10,
            intelligence: 10,
            willPower: 10,
            fellowship: 10,
            magic: 4,
        },
        points: {
            insanity: 1,
            fate: 3,
            fortune: 2,
            wounds: 10,
            maxWounds: 10,
        }
    }
}

async function createValidParty(): Promise<ReturnType<typeof validParty>> {
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

function withoutField<T extends object>(object: T, field: string) : Partial<T> {
    return Object.fromEntries(Object.entries(object).filter(([fieldName]) => fieldName === field));
}

/**
 * Returns Party ID
 */
async function createUserAccessibleParty(userId: string): Promise<string> {
    const party = await createValidParty();

    await setUserInvitation(userId, party.id, party.accessCode);

    authedApp(userId)
        .collection("parties")
        .doc(party.id)
        .set({...party, users: [...party.users, userId]});

    return party.id;
}

/*
 * ============
 *  Test Cases
 * ============
 */
before(async () => {
    await firebase.loadFirestoreRules({projectId, rules});
});

beforeEach(async () => {
    // Clear the database between tests
    await firebase.clearFirestoreData({projectId});
});

after(async () => {
    await Promise.all(firebase.apps().map(app => app.delete()));
    console.log(`View rule coverage information at ${coverageUrl}\n`);
});

@suite
class Database {
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

        // Empty access code
        await firebase.assertFails(parties.doc(data.id).set({...data, accessCode: ""}));

        // Invalid access code type
        await firebase.assertFails(parties.doc(data.id).set({...data, accessCode: 15}));

        // Unknown field
        await firebase.assertFails(parties.doc(data.id).set({...data, otherField: 15}));
    }

    @test
    async "should only let users create party with them being gameMaster"() {
        const data = validParty();
        const party = authedApp(data.gameMasterId)
            .collection("parties")
            .doc(data.id);

        await firebase.assertFails(party.set({...data, gameMasterId: "different-user"}));
        await firebase.assertSucceeds(party.set(data))
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
        const partyId = await createUserAccessibleParty(userId);

        const character = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        await firebase.assertSucceeds(character.set(validCharacter(userId)));
    }

    async "should NOT let users to create their character in party they DON'T have access to"() {
        const userId = "user123";
        const partyId = await createUserAccessibleParty("another-user");

        const character = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        await firebase.assertFails(character.set(validCharacter(userId)));
    }

    async "should not let users create incomplete character"() {
        const userId = "user123";
        const partyId = await createUserAccessibleParty(userId);

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
        const partyId = await createUserAccessibleParty(userId);

        const character = authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);


        const data = validCharacter(userId);

        // Empty character name
        await firebase.assertFails(character.set({...data, name: ""}));

        // Whitespaces only name
        await firebase.assertFails(character.set({...data, name: "\t \r"}));

        // User ID not matching document key
        await firebase.assertFails(character.set({...data, userId: "foo"}));

        // Empty career
        await firebase.assertFails(character.set({...data, career: ""}));

        // Whitespaces only career
        await firebase.assertFails(character.set({...data, career: "\t \r"}));

        // Invalid race
        await firebase.assertFails(character.set({...data, race: "ORC"}));

        // Extra field
        await firebase.assertFails(character.set({...data, extraField: "foo"}));
    }

    @test
    async "should not let users create character with invalid stats"() {
        const userId = "user123";
        const partyId = await createUserAccessibleParty(userId);

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

            newData.stats[stat] = -1;

            // Negative stat
            await firebase.assertFails(character.set(newData));
        }
    }

    @test
    async "should not let users create character with invalid points"() {
        const userId = "user123";
        const partyId = await createUserAccessibleParty(userId);

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

        await firebase.assertSucceeds(character.set(withPoints('wounds', data.points.maxWounds - 1)));
        await firebase.assertSucceeds(character.set(withPoints('wounds', data.points.maxWounds)));
        await firebase.assertFails(character.set(withPoints('wounds', data.points.maxWounds + 1)));
    }
}
