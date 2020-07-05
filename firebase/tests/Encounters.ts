import {suite} from "mocha-typescript";
import {Suite} from "./Suite";
import {uuid} from "uuidv4";
import {assertFails, assertSucceeds} from "@firebase/testing";
import {withoutField} from "./utils";

interface Encounter {
    id: string;
    name: string;
    description: string;
    position: number;
    completed: boolean;
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
