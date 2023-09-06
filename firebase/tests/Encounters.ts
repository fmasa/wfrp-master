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
    async "GM can initiate combat"() {
        const party = await this.createValidParty();
        const encounter = this.validEncounter();

        const partyDocument = this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id);

        await partyDocument
            .collection("encounters")
            .doc(encounter.id)
            .set(encounter);

        await assertSucceeds(partyDocument.update("activeCombat", {
            turn: 1,
            round: 1,
            encounterId: encounter.id,
            combatants: [
                {
                    id: uuid(),
                    characterId: "123",
                    advantage: 0,
                    initiative: 1,
                },
            ]}
        ));
        await assertSucceeds(partyDocument.update("activeCombat", null));
    }

    @test
    async "GM CANNOT initiate combat for encounter that does not exist"() {
        const party = await this.createValidParty();

        const partyDocument = this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id);

        await assertFails(partyDocument.update("activeCombat", {
            turn: 1,
            round: 1,
            encounterId: uuid(),
            combatants: [],
        }));
    }

    /**
     * This is used so that players can change their advantage
     */
    @test
    async "Player can update combatants"() {
        const playerId = "user123";

        const party = await this.createUserAccessibleParty(playerId);
        const encounter = this.validEncounter();

        const partyDocument = this.authedApp(this.validPartyGameMasterId)
            .collection("parties")
            .doc(party.id);

        await partyDocument
            .collection("encounters")
            .doc(encounter.id)
            .set(encounter);

        await partyDocument.update("activeCombat", {
            turn: 1,
            round: 1,
            encounterId: encounter.id,
            combatants: [{userId: "123"}]
        });

        await assertSucceeds(
            this.authedApp(playerId)
                .collection("parties")
                .doc(party.id)
                .update("activeCombat", {
                    turn: 1,
                    round: 1,
                    encounterId: encounter.id,
                    combatants: [{userId: "124"}],
                })
        );
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
}
