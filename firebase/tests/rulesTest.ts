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

        await firebase.assertFails(party.set({...data, name: ""}));
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
}
