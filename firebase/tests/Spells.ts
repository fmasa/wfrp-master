import {suite, test} from "mocha-typescript";
import {CharacterSubCollectionSuite} from "./CharacterSubCollectionSuite";
import {uuid} from "uuidv4";
import * as firebase from "@firebase/testing";
import {CollectionReference, Firestore} from "./firebase";

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
            const spells = this.spells(this.authedApp(userId), this.userId1);

            await firebase.assertSucceeds(spells.doc(this.spell.id).set(this.spell));
        }
    }

    @test
    async "other users CANNOT add spell to character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const spells = this.spells(this.authedApp(userId), this.userId1);

            await firebase.assertFails(spells.doc(this.spell.id).set(this.spell));
        }
    }

    @test
    async "user (and GM) can update spell of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const spells = this.spells(this.authedApp(userId), this.userId1);
            await spells.doc(this.spell.id).set(this.spell);

            await firebase.assertSucceeds(spells.doc(this.spell.id).set({effect: "Improved effect."}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update spell of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.spells(this.authedApp(this.userId1), this.userId1).doc(this.spell.id).set(this.spell);
            const talents = this.spells(this.authedApp(userId), this.userId1);

            await firebase.assertFails(talents.doc(this.spell.id).set({effect: "Improved effect."}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove spell of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const spells = this.spells(this.authedApp(userId), this.userId1);
            await spells.doc(this.spell.id).set(this.spell);

            await firebase.assertSucceeds(spells.doc(this.spell.id).delete());
        }
    }

    @test
    async "other users CANNOT remove spell of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.spells(this.authedApp(this.userId1), this.userId1).doc(this.spell.id).set(this.spell);
            const spells = this.spells(this.authedApp(userId), this.userId1);

            await firebase.assertFails(spells.doc(this.spell.id).delete());
        }
    }

    @test
    async "all party members can read spells"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.spells(this.authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "spell with field missing CANNOT be saved"() {
        const spells = this.spells(this.authedApp(this.userId1), this.userId1);

        await Promise.all(Object.keys(this.spell).map(field => {
            const spell = {...this.spell};
            const spellId = spell.id;

            delete spell[field];

            return firebase.assertFails(spells.doc(spellId).set(spell));
        }));
    }

    @test
    async "spell with invalid field CANNOT be saved"() {
        const spellDoc = this.spells(this.authedApp(this.userId1), this.userId1).doc(this.spell.id);

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
