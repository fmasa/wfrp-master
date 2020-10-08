import {suite} from "mocha-typescript";
import {Suite} from "./Suite";
import {uuid} from "uuidv4";
import {assertFails, assertSucceeds} from "@firebase/testing";
import {withoutField} from "./utils";
import {Armor, Conditions, Party, Stats} from "../api";
import {CollectionReference} from "./firebase";

interface Encounter {
    id: string;
    name: string;
    description: string;
    position: number;
    completed: boolean;
}

interface Npc {
    id: string;
    name: string;
    note: string;
    enemy: boolean;
    alive: boolean;
    trappings: string[];
    traits: string[];
    stats: Stats;
    wounds: {
        max: number;
        current: number;
    },
    armor: Armor;
    position: number;
    conditions: Conditions;
}

@suite
class Encounters extends Suite {
    @test
    async "GM can create encounters"() {
        const party = await this.createValidParty();
        const encounter = this.validEncounter();

        await assertSucceeds(
            this.authedApp(this.validPartyGameMasterId)
                .collection("parties")
                .doc(party.id)
                .collection("encounters")
                .doc(encounter.id)
                .set(encounter)
        )
    }

    @test
    async "GM can remove encounters"() {
        const party = await this.createValidParty();
        const encounter = this.validEncounter();

        const document = this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection("encounters")
            .doc(encounter.id);

        await document.set(encounter)

        await assertSucceeds(document.delete())
    }

    @test
    async "Player CANNOT create encounters"() {
        const userId = "user123"
        const party = await this.createUserAccessibleParty(userId)
        const encounter = this.validEncounter();


        await assertFails(
            this.authedApp(userId)
                .collection("parties")
                .doc(party.id)
                .collection("encounters")
                .doc(encounter.id)
                .set(encounter)
        )
    }

    @test
    async "CANNOT create incomplete encounters"() {
        const party = await this.createValidParty();
        const encounter = this.validEncounter();

        const document = this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection("encounters")
            .doc(encounter.id);

        await Promise.all(
            Object.keys(encounter).map(field => assertFails(document.set(withoutField(encounter, field))))
        )
    }

    @test
    async "GM can add combatant"() {
        const combatantsCollection = await this.combatantCollection();

        const npc = this.validNpc();
        await assertSucceeds(combatantsCollection.doc(npc.id).set(npc));

        // conditions are introduced in 1.X and should be optional for BC (TODO: Remove later)
        const npc2 = withoutField(this.validNpc(), "conditions");
        await assertSucceeds(combatantsCollection.doc(npc2.id).set(npc2));
    }

    @test
    async "GM can remove combatant"() {
        const combatantsCollection = await this.combatantCollection();
        const combatant = this.validNpc();
        const document = combatantsCollection.doc(combatant.id);

        await document.set(combatant);

        await assertSucceeds(document.delete());
    }

    @test
    async "Player CANNOT add combatant"() {
        const party = await this.createUserAccessibleParty("user123");
        const encounter = await this.createValidEncounter(party);

        const npcsCollection = this.authedApp("user123")
            .collection("parties")
            .doc(party.id)
            .collection("encounters")
            .doc(encounter.id)
            .collection("combatants");

        const npc = this.validNpc();

        await assertFails(npcsCollection.doc(npc.id).set(npc));
    }

    private async combatantCollection(): Promise<CollectionReference> {
        const party = await this.createValidParty();
        const encounter = this.validEncounter();

        const document = this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection("encounters")
            .doc(encounter.id);

        await document.set(encounter)

        return document.collection("combatants");
    }

    private async createValidEncounter(party: Party): Promise<Encounter> {
        const encounter = this.validEncounter();
        const document = this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id)
            .collection("encounters")
            .doc(encounter.id);

        await document.set(encounter);

        return encounter;
    }

    private validEncounter(): Encounter {
        return {
            id: uuid(),
            name: "First battle",
            description: "This takes place in sewers. Visibility is low.",
            position: 0,
            completed: false,
        }
    }

    private validNpc(): Npc {
        return {
            id: uuid(),
            name: "Toby",
            note: "",
            enemy: true,
            alive: true,
            trappings: ["short sword"],
            traits: ["Fear 1", "Luck 1"],
            stats: {
                agility: 20,
                ballisticSkill: 12,
                dexterity: 15,
                fellowship: 10,
                initiative: 40,
                intelligence: 64,
                strength: 32,
                toughness: 15,
                weaponSkill: 13,
                willPower: 10,
            },
            armor: {
                head: 1,
                body: 1,
                leftArm: 1,
                rightArm: 1,
                leftLeg: 1,
                rightLeg: 1,
                shield: 1,
            },
            wounds: {
                current: 2,
                max: 5,
            },
            position: 0,
            conditions: {
                conditions: {}
            }
        }
    }
}
