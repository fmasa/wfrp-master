import {suite, test} from "mocha-typescript";
import {CharacterSubCollectionSuite} from "./CharacterSubCollectionSuite";
import {uuid} from "uuidv4";
import * as firebase from "@firebase/testing";
import {CollectionReference, Firestore} from "./firebase";

@suite
class Skills extends CharacterSubCollectionSuite {
    private skill = {
        id: uuid(),
        advanced: false,
        characteristic: "FELLOWSHIP",
        name: "Haggle",
        description: "Lower the price of goods",
        advances: 1,
        compendiumId: null,
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
            const skills = this.skills(this.authedApp(userId), this.userId1);

            await firebase.assertSucceeds(skills.doc(this.skill.id).set(this.skill));
        }
    }

    @test
    async "other users CANNOT add skill to character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const skills = this.skills(this.authedApp(userId), this.userId1);

            await firebase.assertFails(skills.doc(this.skill.id).set(this.skill));
        }
    }

    @test
    async "user (and GM) can update skills of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const skills = this.skills(this.authedApp(userId), this.userId1);
            await skills.doc(this.skill.id).set(this.skill);

            await firebase.assertSucceeds(skills.doc(this.skill.id).set({advanced: true}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update skill of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.skills(this.authedApp(this.userId1), this.userId1).doc(this.skill.id).set(this.skill);
            const skills = this.skills(this.authedApp(userId), this.userId1);

            await firebase.assertFails(skills.doc(this.skill.id).set({advanced: true}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove skill of his character"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const skills = this.skills(this.authedApp(userId), this.userId1);
            await skills.doc(this.skill.id).set(this.skill);

            await firebase.assertSucceeds(skills.doc(this.skill.id).delete());
        }
    }

    @test
    async "other users CANNOT remove skill of character"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.skills(this.authedApp(this.userId1), this.userId1).doc(this.skill.id).set(this.skill);
            const skills = this.skills(this.authedApp(userId), this.userId1);

            await firebase.assertFails(skills.doc(this.skill.id).delete());
        }
    }

    @test
    async "all party members can read skills"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.skills(this.authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "skill with field missing CANNOT be saved"() {
        const skills = this.skills(this.authedApp(this.userId1), this.userId1);

        for (const field of Object.keys(this.skill)) {
            if (field === "compendiumId") {
                continue; // (BC) Compendium ID was introduced in 2.0 (TODO: remove in 2.3)
            }

            const skill = {...this.skill};
            const skillId = skill.id;

            delete skill[field];

            await firebase.assertFails(skills.doc(skillId).set(skill));
        }
    }

    @test
    async "skill with ID of existing compendium skill can be created"() {
        const compendiumSkill = {
            id: uuid(),
            advanced: false,
            characteristic: "FELLOWSHIP",
            name: "Haggle",
            description: "Lower the price of goods",
        };

        await this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(this.partyId)
            .collection("skills")
            .doc(compendiumSkill.id)
            .set(compendiumSkill);

        const skill = {...this.skill, compendiumId: compendiumSkill.id}

        await firebase.assertSucceeds(
            this.skills(this.authedApp(this.userId1), this.userId1)
                .doc(skill.id)
                .set(skill)
        );
    }

    @test
    async "skill with invalid field CANNOT be saved"() {
        const skillDoc = this.skills(this.authedApp(this.userId1), this.userId1).doc(this.skill.id);

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
                {description: "a".repeat(2001)},

                // Invalid compendium ID
                {compendiumId: "foo"},

                // Nonexistent compendium skill
                {compendiumId: uuid()},

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
