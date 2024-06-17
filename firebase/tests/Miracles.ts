import {suite, test} from "mocha-typescript";
import {CharacterSubCollectionSuite} from "./CharacterSubCollectionSuite";
import {uuid} from "uuidv4";
import * as firebase from "@firebase/testing";
import {CollectionReference, Firestore} from "./firebase";

interface Miracle {
    id: string;
    name: string;
    range: string;
    target: string;
    duration: string;
    effect: string;
    cultName: string;
    compendiumId: string | null;
}

@suite
class Miracles extends CharacterSubCollectionSuite {
    private miracle = {
          id: uuid(),
          name: "Becalm",
          range: "IB miles",
          target: "1 sailing vessel...",
          duration: "1 Hour",
          effect: "Calm waters around your ship",
          cultName: "Manann",
          compendiumId: null,
    };

    private miracles(app: Firestore, userId: string): CollectionReference
    {
        return app.collection("parties")
            .doc(this.partyId)
            .collection("characters")
            .doc(userId)
            .collection("miracles");
    }

    @test
    async "user (and GM) can add miracle to his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const miracles = this.miracles(this.authedApp(userId), this.userId1);

            await firebase.assertSucceeds(miracles.doc(this.miracle.id).set(this.miracle));
        }
    }

    @test
    async "other users CANNOT add miracle to character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const miracles = this.miracles(this.authedApp(userId), this.userId1);

            await firebase.assertFails(miracles.doc(this.miracle.id).set(this.miracle));
        }
    }

    @test
    async "user (and GM) can update miracles of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const miracles = this.miracles(this.authedApp(userId), this.userId1);
            await miracles.doc(this.miracle.id).set(this.miracle);

            await firebase.assertSucceeds(miracles.doc(this.miracle.id).set({name: "Renamed miracle"}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update miracle of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.miracles(this.authedApp(this.userId1), this.userId1).doc(this.miracle.id).set(this.miracle);
            const miracles = this.miracles(this.authedApp(userId), this.userId1);

            await firebase.assertFails(miracles.doc(this.miracle.id).set({name: "Renamed miracle"}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove miracle of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const miracles = this.miracles(this.authedApp(userId), this.userId1);
            await miracles.doc(this.miracle.id).set(this.miracle);

            await firebase.assertSucceeds(miracles.doc(this.miracle.id).delete());
        }
    }

    @test
    async "other users CANNOT remove miracle of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.miracles(this.authedApp(this.userId1), this.userId1).doc(this.miracle.id).set(this.miracle);
            const miracles = this.miracles(this.authedApp(userId), this.userId1);

            await firebase.assertFails(miracles.doc(this.miracle.id).delete());
        }
    }

    @test
    async "all party members can read miracles"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.miracles(this.authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "miracle with field missing CANNOT be saved"() {
        const miracles = this.miracles(this.authedApp(this.userId1), this.userId1);

        for (const field of Object.keys(this.miracle)) {
            const miracle = {...this.miracle};
            const miracleId = miracle.id;

            delete miracle[field];

            await firebase.assertFails(miracles.doc(miracleId).set(miracle));
        }
    }

    @test
    async "miracle with ID of existing compendium miracle can be created"() {
        const compendiumMiracle = {
            id: uuid(),
            name: "Becalm",
            range: "IB miles",
            target: "1 sailing vessel...",
            duration: "1 Hour",
            effect: "Calm waters around your ship",
            cultName: "Manann",
        };

        await this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(this.partyId)
            .collection("miracles")
            .doc(compendiumMiracle.id)
            .set(compendiumMiracle);

        const miracle = {...this.miracle, compendiumId: compendiumMiracle.id}

        await firebase.assertSucceeds(
            this.miracles(this.authedApp(this.userId1), this.userId1)
                .doc(miracle.id)
                .set(miracle)
        );
    }

    @test
    async "miracle with invalid field CANNOT be saved"() {
        const miracleDoc = this.miracles(this.authedApp(this.userId1), this.userId1).doc(this.miracle.id);

        await Promise.all(
            [
                // ID not matching document ID
                {id: uuid()},

                // ID is not valid UUID
                {id: "foo"},

                // Empty name
                {name: ""},

                // Range too long
                {range: "a".repeat(51)},

                // Duration too late
                {duration: "a".repeat(51)},

                // Cult name too late
                {cultName: "a".repeat(51)},

                // Name too long
                {name: "a".repeat(51)},

                // Target too long
                {target: "a".repeat(201)},

                // Description too long
                {effect: "a".repeat(1001)},

                // Invalid compendium ID
                {compendiumId: "foo"},

                // Nonexistent compendium miracle
                {compendiumId: uuid()},



            ].map(doc => firebase.assertFails(miracleDoc.set({...this.miracle, ...doc})))
        );

        await Promise.all(Object.keys(this.miracle).map(field => {
            return firebase.assertFails(
                miracleDoc.set({
                    ...this.miracle,
                    [field]: typeof this.miracle[field] == 'string' ? true : 'foo',
                })
            );
        }));
    }
}
