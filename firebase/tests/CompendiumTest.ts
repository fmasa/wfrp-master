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

interface Blessing {
    id: string;
    name: string;
    range: string;
    target: string;
    duration: string;
    effect: string;
}

interface Miracle {
    id: string;
    name: string;
    range: string;
    target: string;
    duration: string;
    effect: string;
    cultName: string;
}

interface Spell {
    id: string;
    name: string;
    range: string;
    target: string;
    duration: string;
    castingNumber: number;
    effect: string;
    lore: string;
}

interface Career {
    id: string;
    name: string;
    description: string;
    socialClass: string;
    races: string[];
    levels: [
        CareerLevel,
        CareerLevel,
        CareerLevel,
        CareerLevel,
    ]
}

interface CareerLevel {
    name: string;
    upgradableCharacteristics: string[];
    skillIds: string[];
    talentIds: string[];
    status: {
        tier: string,
        standing: number,
    }
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
class BlessingCompendiumTest extends CompendiumTest<Blessing> {
    protected collectionName = "blessings";

    protected validItems(): Blessing[] {
        return [
            {
                id: uuid(),
                name: "Blessing of Battle",
                range: "6 yards",
                target: "1",
                duration: "6 rounds",
                effect: "Your target gains +10 WS",
            }
        ]
    }
}

@suite
class MiracleCompendiumTest extends CompendiumTest<Miracle> {
    protected collectionName = "miracles";

    protected validItems(): Miracle[] {
        return [
            {
                id: uuid(),
                name: "Becalm",
                range: "IB miles",
                target: "1 sailing vessel...",
                duration: "1 Hour",
                effect: "Calm waters around your ship",
                cultName: "Manann",
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
                effect: "Your staff blinks with bright white light which blinds target for 1d10 minutes",
                lore: "Petty spells",
            }
        ]
    }
}
@suite
class CareerCompendiumTest extends CompendiumTest<Career> {
    protected collectionName = "careers";

    protected validItems(): Career[] {
        return [
            {
                id: uuid(),
                name: "Rat catcher",
                description: "Rat catchers catch rats. Duh!",
                socialClass: "PEASANTS",
                races: ["HUMAN"],
                levels: [
                    {
                        name: "Level 1",
                        upgradableCharacteristics: ["WILL_POWER", "DEXTERITY"],
                        skillIds: [uuid(), uuid()],
                        talentIds: [uuid(), uuid()],
                        status: {
                            tier: "BRASS",
                            standing: 1,
                        },
                    },
                    {
                        name: "Level 2",
                        upgradableCharacteristics: ["WEAPON_SKILL", "INITIATIVE"],
                        skillIds: [uuid(), uuid()],
                        talentIds: [uuid(), uuid()],
                        status: {
                            tier: "BRASS",
                            standing: 1,
                        },
                    },
                    {
                        name: "Level 3",
                        upgradableCharacteristics: ["BALLISTIC_SKILL", "INTELLIGENCE", "WILL_POWER"],
                        skillIds: [uuid(), uuid()],
                        talentIds: [uuid(), uuid()],
                        status: {
                            tier: "BRASS",
                            standing: 1,
                        },
                    },
                    {
                        name: "Level 4",
                        upgradableCharacteristics: ["FELLOWSHIP", "STRENGTH", "TOUGHNESS"],
                        skillIds: [uuid(), uuid()],
                        talentIds: [uuid(), uuid()],
                        status: {
                            tier: "BRASS",
                            standing: 1,
                        },
                    }
                ]
            }
        ]
    }
}
