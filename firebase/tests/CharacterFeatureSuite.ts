import {CharacterSubCollectionSuite} from "./CharacterSubCollectionSuite";
import {test} from "mocha-typescript";
import * as firebase from "@firebase/testing";
import {withoutField} from "./utils";

export abstract class CharacterFeatureSuite extends CharacterSubCollectionSuite {
    abstract getFeatureName(): string
    abstract getValue(): object

    private getDocument(app: firebase.firestore.Firestore, userId: string) {
        return app.collection("parties")
            .doc(this.partyId)
            .collection("characters")
            .doc(userId)
            .collection("features")
            .doc(this.getFeatureName());
    }

    @test
    async "Can be edited by user"() {
        await firebase.assertSucceeds(
            this.getDocument(this.authedApp(this.userId1), this.userId1)
                .set(this.getValue())
        );
    }

    @test
    async "Can be edited by GM"() {
        await firebase.assertSucceeds(
            this.getDocument(this.authedApp(this.gameMasterId), this.userId1)
                .set(this.getValue())
        );
    }

    @test
    async "Cannot be edited by other users"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await firebase.assertFails(
                this.getDocument(this.authedApp(userId), this.userId1)
                    .set(this.getValue())
            );
        }
    }

    @test
    async "Cannot miss a field"() {
        for (const field of Object.keys(this.getValue())) {
            await firebase.assertFails(
                this.getDocument(this.authedApp(this.userId1), this.userId1)
                    .set(withoutField(this.getValue(), field))
            );
        }
    }
}
