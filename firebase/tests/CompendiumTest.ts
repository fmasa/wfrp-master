import {suite} from "mocha-typescript";
import {Suite} from "./Suite";
import {uuid} from "uuidv4";
import {assertFails, assertSucceeds} from "@firebase/testing";
import {withoutField} from "./utils";

interface Skill {
    id: string;
    advanced: boolean;
    characteristic: string;
    name: string;
    description: string;
}

interface Talent {
    id: string;
    name: string;
    maxTimesTaken: string;
    description: string;
}

interface Spell {
    id: string;
    name: string;
    range: string;
    target: string;
    duration: string;
    castingNumber: number;
    effect: string;
}

abstract class CompendiumTest<TItem extends { id: string }> extends Suite {

    protected abstract collectionName: string

    protected abstract validItems(): TItem[]

    @test
    async "GM can create items"() {
        const party = await this.createValidParty();
        const items = this.validItems();

        for (const item of items) {
            await assertSucceeds(
                this.authedApp(this.validPartyGameMasterId)
                    .collection("parties")
                    .doc(party.id)
                    .collection(this.collectionName)
                    .doc(item.id)
                    .set(item)
            )
        }
    }

    async "Players CANNOT create items"() {
        const party = await this.createUserAccessibleParty("player1");
        const item = this.validItems()[0];

        await assertFails(
            this.authedApp("player1")
                .collection("parties")
                .doc(party.id)
                .collection(this.collectionName)
                .doc(item.id)
                .set(item)
        )
    }

    @test
    async "GM CANNOT create incomplete items"() {
        const party = await this.createValidParty();
        const item = this.validItems()[0];

        for (const field of Object.keys(item)) {
            await assertFails(
                this.authedApp(this.validPartyGameMasterId)
                    .collection("parties")
                    .doc(party.id)
                    .collection(this.collectionName)
                    .doc(item.id)
                    .set(withoutField(item, field))
            )
        }
    }

    @test
    async "Players can read items"() {
        const party = await this.createValidParty();
        const item = this.validItems()[0];

        await this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection(this.collectionName)
            .doc(item.id)
            .set(item);

        await assertSucceeds(
            this.authedApp(this.validPartyGameMasterId)
                .collection("parties")
                .doc(party.id)
                .collection(this.collectionName)
                .doc(item.id)
                .get()
        )
    }

    @test
    async "GM can update items"() {
        const party = await this.createValidParty();
        const item = this.validItems()[0];

        const document = this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection(this.collectionName)
            .doc(item.id);

        await document.set(item)
        await assertSucceeds(document.update({}))
    }

    @test
    async "Players CANNOT update items"() {
        const party = await this.createUserAccessibleParty("player1");
        const item = this.validItems()[0];

        await this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection(this.collectionName)
            .doc(item.id)
            .set(item)

        await assertFails(
            this.authedApp("player1")
                .collection("parties")
                .doc(party.id)
                .collection(this.collectionName)
                .doc(item.id)
                .update({})
        );
    }

    @test
    async "GM can delete items"() {
        const party = await this.createValidParty();
        const item = this.validItems()[0];

        const document = this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection(this.collectionName)
            .doc(item.id);

        await document.set(item)
        await assertSucceeds(document.delete())
    }

    @test
    async "Players CANNOT delete items"() {
        const party = await this.createUserAccessibleParty("player1");
        const item = this.validItems()[0];

        await this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection(this.collectionName)
            .doc(item.id)
            .set(item)

        await assertFails(
            this.authedApp("player1")
                .collection("parties")
                .doc(party.id)
                .collection(this.collectionName)
                .doc(item.id)
                .delete()
        );
    }
}

@suite
class SkillCompendiumTest extends CompendiumTest<Skill> {
    protected collectionName = "skills";

    protected validItems(): Skill[] {
        return [
            {
                id: uuid(),
                advanced: false,
                characteristic: "FELLOWSHIP",
                name: "Haggle",
                description: "Lower the price of goods",
            }
        ]
    }
}

@suite
class TalentCompendiumTest extends CompendiumTest<Talent> {
    protected collectionName = "talents";

    protected validItems(): Talent[] {
        return [
            {
                id: uuid(),
                name: "Sneaky brieky",
                maxTimesTaken: "Fellowship Bonus",
                description: "+ 1O to sneak rolls",
            }
        ]
    }
}

@suite
class SpellCompendiumTest extends CompendiumTest<Spell> {
    protected collectionName = "spells";

    protected validItems(): Spell[] {
        return [
            {
                id: uuid(),
                name: "Blink",
                range: "1 yard",
                target: "1",
                duration: "3 seconds",
                castingNumber: 0,
                effect: "Your staff blinks with bright white light which blinds target for 1d10 minutes"
            }
        ]
    }
}
