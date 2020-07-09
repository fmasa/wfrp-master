import {suite} from "mocha-typescript";
import {CharacterFeatureSuite} from "./CharacterFeatureSuite";
import {Armor} from "./api";

@suite
class ArmorTest extends CharacterFeatureSuite {
    getFeatureName(): string {
        return "armor";
    }

    getValue(): Armor {
        return {
            head: 1,
            body: 1,
            leftArm: 1,
            rightArm: 1,
            leftLeg: 1,
            rightLeg: 1,
            shield: 1,
        }
    }
}
