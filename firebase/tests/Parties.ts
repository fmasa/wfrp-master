import {suite, test} from "mocha-typescript";
import {Suite} from "./Suite";
import * as firebase from "@firebase/testing";
import {uuid} from "uuidv4";
import {withoutField} from "./utils";

@suite
class Parties extends Suite {
    @test
    async "require users to log in before creating a party"() {
        const data = this.validParty();
        const party = this.authedApp(null)
            .collection("parties")
            .doc(data.id);

        await firebase.assertFails(party.set(data));
    }

    @test
    async "require users to give name to a new party"() {
        const data = this.validParty();
        const party = this.authedApp(data.gameMasterId)
            .collection("parties")
            .doc(data.id);

        // Empty name
        await firebase.assertFails(party.set({...data, name: ""}));

        // Whitespaces only name
        await firebase.assertFails(party.set({...data, name: "\t \r"}));

        await firebase.assertSucceeds(party.set(data))
    }

    @test
    async "users can create party without `archived` field (BC)"() {
        const data = this.validParty();

        delete data['archived'];

        const party = this.authedApp(data.gameMasterId)
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
        const data = this.validParty();
        const parties = this.authedApp(data.gameMasterId).collection("parties");

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
        const data = this.validParty();
        const party = this.authedApp(data.gameMasterId)
            .collection("parties")
            .doc(data.id);

        await firebase.assertFails(party.set({...data, gameMasterId: "different-user"}));
        await firebase.assertSucceeds(party.set(data))
    }

    @test
    async "should let users create party without GM (AKA Single-player party)"() {
        const data = this.validParty();
        const party = this.authedApp(data.gameMasterId)
            .collection("parties")
            .doc(data.id);

        await firebase.assertSucceeds(party.set({...data, gameMasterId: null}));
    }

    @test
    async "should only let users create party with them having access"() {
        const data = this.validParty();
        const party = this.authedApp(data.gameMasterId)
            .collection("parties")
            .doc(data.id);

        await firebase.assertFails(party.set({...data, users: ["different-user"]}));
        await firebase.assertFails(party.set({...data, users: [...data.users, "different-user"]}));
        await firebase.assertSucceeds(party.set(data));
    }

    @test
    async "should NOT let users create party with any of mandatory fields missing"() {
        const parties = this.authedApp(this.validPartyGameMasterId)
            .collection("parties");

        await Promise.all(Object.keys(this.validParty()).map(field => {
            if (field === 'archived') {
                return; // This field is optional for now (BC)
            }

            const party = this.validParty();
            const partyId = party.id;

            delete party[field];

            return firebase.assertFails(parties.doc(partyId).set(party));
        }));
    }

    @test
    async "should only let users join party with an valid invitation"() {
        const party = await this.createValidParty();
        const userId = "joiningUser123";
        const database = this.authedApp(userId);

        const joinParty = () => database.collection("parties")
            .doc(party.id)
            .set({...party, users: [...party.users, userId]});

        // Wrong access code
        await this.setUserInvitation(userId, party.id, "differentAccessCode");
        await firebase.assertFails(joinParty());

        // Different party ID
        await this.setUserInvitation(userId, uuid(), party.accessCode);
        await firebase.assertFails(joinParty());

        // Valid join
        await this.setUserInvitation(userId, party.id, party.accessCode);
        await firebase.assertSucceeds(joinParty());
    }

    @test
    async "should NOT let users add different users to parties"() {
        const party = await this.createValidParty();
        const userId = "user123";

        await this.setUserInvitation(userId, party.id, party.accessCode);
        await firebase.assertFails(
            this.authedApp("differentUserId")
                .collection("parties")
                .doc(party.id)
                .set({...party, users: [...party.users, userId]})
        );
    }

    @test
    async "should NOT let users write total garbage to his document"() {
        const userId = "user123";

        const user = this.authedApp(userId)
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
        const partyId = (await this.createUserAccessibleParty(userId)).id;

        const character = this.authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        await firebase.assertSucceeds(character.set(this.validCharacter(userId)));
    }

    @test
    async "should NOT let users to create their character in party they DON'T have access to"() {
        const userId = "user123";
        const partyId = (await this.createUserAccessibleParty("another-user")).id;

        const character = this.authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        await firebase.assertFails(character.set(this.validCharacter(userId)));
    }

    @test
    async "should not let users create incomplete character"() {
        const userId = "user123";
        const partyId = (await this.createUserAccessibleParty(userId)).id;

        const character = this.authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        const data = this.validCharacter(userId);

        for (const field of Object.keys(data)) {
            const newData = {...data};

            delete newData[field];

            await firebase.assertFails(character.set(newData));
        }
    }

    @test
    async "should not let users create character with invalid data"() {
        const userId = "user123";
        const partyId = (await this.createUserAccessibleParty(userId)).id;

        const character = this.authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);


        const data = this.validCharacter(userId);

        // Empty character name
        await firebase.assertFails(character.set({...data, name: ""}));

        // Character name too long
        await firebase.assertFails(character.set({...data, name: "a".repeat(51)}));

        // Whitespaces only name+
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

        // Psychology too long
        await firebase.assertFails(character.set({...data, psychology: "a".repeat(201)}));

        // Motivation too long
        await firebase.assertFails(character.set({...data, motivation: "a".repeat(201)}));

        // Mutation too long
        await firebase.assertFails(character.set({...data, mutation: "a".repeat(201)}));

        // Negative amount of money
        await firebase.assertFails(character.set({...data, money: {pennies: -1}}));

        // Invalid money
        await firebase.assertFails(character.set({...data, money: {}}));

        // Extra field
        await firebase.assertFails(character.set({...data, extraField: "foo"}));

        // Invalid ambition object type
        await firebase.assertFails(character.set({...data, ambitions: "foo"}));

        // Additional notes too long
        await firebase.assertFails(character.set({...data, note: "a".repeat(401)}));

        // Invalid ambition field type
        await firebase.assertFails(character.set({...data, ambitions: {shortTerm: "foo", longTerm: 1}}));
    }

    @test
    async "should not let users create character with invalid stats"() {
        const userId = "user123";
        const partyId = (await this.createUserAccessibleParty(userId)).id;

        const character = this.authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        const data = this.validCharacter(userId);

        for (const stat of Object.keys(data.stats)) {
            const newData = {...data, stats: withoutField(data.stats, stat)};

            // Missing stat
            await firebase.assertFails(character.set(newData));

            // Wrong stat type
            newData.stats[stat] = "foo";
            await firebase.assertFails(character.set(newData));
        }
    }

    @test
    async "should not let users create character with invalid points"() {
        const userId = "user123";
        const partyId = (await this.createUserAccessibleParty(userId)).id;

        const character = this.authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        const data = this.validCharacter(userId);

        await firebase.assertFails(character.set({...data, stats: {...data.stats, extraStat: 10}}));

        const withPoints = (point: string, value: any) => ({...data, points: {...data.points, [point]: value}});

        for (const point in data.points) {
            // Negative point
            await firebase.assertFails(character.set(withPoints(point, "foo")));
        }
    }

    @test
    async "should let users update their character"() {
        const userId = "user123";
        const partyId = (await this.createUserAccessibleParty(userId)).id;
        await this.createCharacter(partyId, userId);

        const document = this.authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        await firebase.assertSucceeds(document.set({points: {resilience: 10}}, {merge: true}));
        await firebase.assertSucceeds(document.update("note", "a".repeat(400)));
        await firebase.assertSucceeds(document.update("hardyTalent", true));
        await firebase.assertSucceeds(
            document.update("points", {...this.validCharacter(userId).points, hardyWoundsBonus: 3})
        );
    }

    @test
    async "should let game master update character"() {
        const userId = "user123";
        const party = await this.createUserAccessibleParty(userId);
        await this.createCharacter(party.id, userId);

        const document = this.authedApp(party.gameMasterId)
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

        const party = await this.createUserAccessibleParty(userId);
        await this.createCharacter(party.id, userId);
        await this.joinParty(party, otherUserId);

        const document = this.authedApp(otherUserId)
            .collection("parties")
            .doc(party.id)
            .collection("characters")
            .doc(userId);

        await firebase.assertFails(document.set({points: {resilience: 10}}, {merge: true}));
    }

    @test
    async "should let GM edit ambitions"() {
        const party = await this.createValidParty();

        const document = this.authedApp(party.gameMasterId)
            .collection("parties")
            .doc(party.id);

        await firebase.assertSucceeds(
            document.update("ambitions", {shortTerm: "Completely new ambitions!", longTerm: "We don't think that far"})
        )
    }

    @test
    async "should let GM rename party"() {
        const party = await this.createValidParty();

        const document = this.authedApp(party.gameMasterId)
            .collection("parties")
            .doc(party.id);

        await firebase.assertSucceeds(document.update("name", "New cool name"))
    }

    @test
    async "should NOT let player rename party"() {
        const userId = "user123";
        const party = await this.createUserAccessibleParty(userId);

        const document = this.authedApp(userId)
            .collection("parties")
            .doc(party.id);

        await firebase.assertFails(document.update("name", "New cool name"))
    }

    @test
    async "should let GM archive party"() {
        const party = await this.createValidParty();

        const document = this.authedApp(party.gameMasterId)
            .collection("parties")
            .doc(party.id);

        await firebase.assertSucceeds(document.update("archived", true))
    }

    @test
    async "should let user archive single-player party"() {
        const data = this.validParty();
        const party = this.authedApp(data.gameMasterId)
            .collection("parties")
            .doc(data.id);

        await firebase.assertSucceeds(party.set({...data, gameMasterId: null}));

        await firebase.assertSucceeds(party.update("archived", true))
    }

    @test
    async "should NOT let player archive party"() {
        const userId = "user123";
        const party = await this.createUserAccessibleParty(userId);

        const document = this.authedApp(userId)
            .collection("parties")
            .doc(party.id);

        await firebase.assertFails(document.update("archived", true))
    }
}
