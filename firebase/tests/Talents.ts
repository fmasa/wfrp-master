import {suite, test} from "mocha-typescript";
import {CharacterSubCollectionSuite} from "./CharacterSubCollectionSuite";
import {uuid} from "uuidv4";
import * as firebase from "@firebase/testing";
import {CollectionReference, Firestore} from "./firebase";

@suite
class Talents extends CharacterSubCollectionSuite {
    private talent = {
        id: uuid(),
        name: "Sneaky brieky",
        description: "+ 1O to sneak rolls",
        taken: 5,
        compendiumId: null,
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
            const talents = this.talents(this.authedApp(userId), this.userId1);

            await firebase.assertSucceeds(talents.doc(this.talent.id).set(this.talent));
        }
    }

    @test
    async "other users CANNOT add talent to character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const talents = this.talents(this.authedApp(userId), this.userId1);

            await firebase.assertFails(talents.doc(this.talent.id).set(this.talent));
        }
    }

    @test
    async "user (and GM) can update talent of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const talents = this.talents(this.authedApp(userId), this.userId1);
            await talents.doc(this.talent.id).set(this.talent);

            await firebase.assertSucceeds(talents.doc(this.talent.id).set({description: "Better desc."}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update talent of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.talents(this.authedApp(this.userId1), this.userId1).doc(this.talent.id).set(this.talent);
            const talents = this.talents(this.authedApp(userId), this.userId1);

            await firebase.assertFails(talents.doc(this.talent.id).set({advanced: true}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove talent of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const talents = this.talents(this.authedApp(userId), this.userId1);
            await talents.doc(this.talent.id).set(this.talent);

            await firebase.assertSucceeds(talents.doc(this.talent.id).delete());
        }
    }

    @test
    async "other users CANNOT remove talent of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.talents(this.authedApp(this.userId1), this.userId1).doc(this.talent.id).set(this.talent);
            const talents = this.talents(this.authedApp(userId), this.userId1);

            await firebase.assertFails(talents.doc(this.talent.id).delete());
        }
    }

    @test
    async "all party members can read talents"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.talents(this.authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "talent with field missing CANNOT be saved"() {
        const talents = this.talents(this.authedApp(this.userId1), this.userId1);

        for (const field of Object.keys(this.talent)) {
            if (field === "compendiumId") {
                continue; // (BC) Compendium ID was introduced in 2.0 (TODO: remove in 2.3)
            }

            const talent = {...this.talent};
            const talentId = talent.id;

            delete talent[field];

            await firebase.assertFails(talents.doc(talentId).set(talent));
        }
    }

    @test
    async "talent with ID of existing compendium talent can be created"() {
        const compendiumTalent = {
            id: uuid(),
            maxTimesTaken: "1",
            name: "Lucky",
            description: "You got extra fortune points",
        };

        await this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(this.partyId)
            .collection("talents")
            .doc(compendiumTalent.id)
            .set(compendiumTalent);

        const talent = {...this.talent, compendiumId: compendiumTalent.id}

        await firebase.assertSucceeds(
            this.talents(this.authedApp(this.userId1), this.userId1)
                .doc(talent.id)
                .set(talent)
        );
    }

    @test
    async "talent with invalid field CANNOT be saved"() {
        const talentDoc = this.talents(this.authedApp(this.userId1), this.userId1).doc(this.talent.id);

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

                // Invalid compendium ID
                {compendiumId: "foo"},

                // Nonexistent compendium talent
                {compendiumId: uuid()},
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
