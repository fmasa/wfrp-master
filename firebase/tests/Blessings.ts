import {suite, test} from "mocha-typescript";
import {CharacterSubCollectionSuite} from "./CharacterSubCollectionSuite";
import {uuid} from "uuidv4";
import * as firebase from "@firebase/testing";
import {CollectionReference, Firestore} from "./firebase";

interface Blessing {
    id: string;
    name: string;
    range: string;
    target: string;
    duration: string;
    effect: string;
    compendiumId: string | null;
}

@suite
class Blessings extends CharacterSubCollectionSuite {
    private blessing = {
        id: uuid(),
        name: "Blessing of Battle",
        range: "6 yards",
        target: "1",
        duration: "6 rounds",
        effect: "Your target gains +10 WS",
        compendiumId: null,
    };

    private blessings(app: Firestore, userId: string): CollectionReference
    {
        return app.collection("parties")
            .doc(this.partyId)
            .collection("characters")
            .doc(userId)
            .collection("blessings");
    }

    @test
    async "user (and GM) can add blessing to his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const blessings = this.blessings(this.authedApp(userId), this.userId1);

            await firebase.assertSucceeds(blessings.doc(this.blessing.id).set(this.blessing));
        }
    }

    @test
    async "other users CANNOT add blessing to character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const blessings = this.blessings(this.authedApp(userId), this.userId1);

            await firebase.assertFails(blessings.doc(this.blessing.id).set(this.blessing));
        }
    }

    @test
    async "user (and GM) can update blessings of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const blessings = this.blessings(this.authedApp(userId), this.userId1);
            await blessings.doc(this.blessing.id).set(this.blessing);

            await firebase.assertSucceeds(blessings.doc(this.blessing.id).set({name: "Renamed blessing"}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update blessing of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.blessings(this.authedApp(this.userId1), this.userId1).doc(this.blessing.id).set(this.blessing);
            const blessings = this.blessings(this.authedApp(userId), this.userId1);

            await firebase.assertFails(blessings.doc(this.blessing.id).set({name: "Renamed blessing"}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove blessing of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const blessings = this.blessings(this.authedApp(userId), this.userId1);
            await blessings.doc(this.blessing.id).set(this.blessing);

            await firebase.assertSucceeds(blessings.doc(this.blessing.id).delete());
        }
    }

    @test
    async "other users CANNOT remove blessing of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.blessings(this.authedApp(this.userId1), this.userId1).doc(this.blessing.id).set(this.blessing);
            const blessings = this.blessings(this.authedApp(userId), this.userId1);

            await firebase.assertFails(blessings.doc(this.blessing.id).delete());
        }
    }

    @test
    async "all party members can read blessings"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.blessings(this.authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "blessing with field missing CANNOT be saved"() {
        const blessings = this.blessings(this.authedApp(this.userId1), this.userId1);

        for (const field of Object.keys(this.blessing)) {
            const blessing = {...this.blessing};
            const blessingId = blessing.id;

            delete blessing[field];

            await firebase.assertFails(blessings.doc(blessingId).set(blessing));
        }
    }

    @test
    async "blessing with ID of existing compendium blessing can be created"() {
        const compendiumBlessing = {
           id: uuid(),
           name: "Blessing of Battle",
           range: "6 yards",
           target: "1",
           duration: "6 rounds",
           effect: "Your target gains +10 WS",
       };

        await this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(this.partyId)
            .collection("blessings")
            .doc(compendiumBlessing.id)
            .set(compendiumBlessing);

        const blessing = {...this.blessing, compendiumId: compendiumBlessing.id}

        await firebase.assertSucceeds(
            this.blessings(this.authedApp(this.userId1), this.userId1)
                .doc(blessing.id)
                .set(blessing)
        );
    }

    @test
    async "blessing with invalid field CANNOT be saved"() {
        const blessingDoc = this.blessings(this.authedApp(this.userId1), this.userId1).doc(this.blessing.id);

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

                // Duration name too late
                {duration: "a".repeat(51)},

                // Name too long
                {name: "a".repeat(51)},

                // Target too long
                {target: "a".repeat(51)},

                // Description too long
                {effect: "a".repeat(1001)},

                // Invalid compendium ID
                {compendiumId: "foo"},

                // Nonexistent compendium blessing
                {compendiumId: uuid()},



            ].map(doc => firebase.assertFails(blessingDoc.set({...this.blessing, ...doc})))
        );

        await Promise.all(Object.keys(this.blessing).map(field => {
            return firebase.assertFails(
                blessingDoc.set({
                    ...this.blessing,
                    [field]: typeof this.blessing[field] == 'string' ? true : 'foo',
                })
            );
        }));
    }
}
