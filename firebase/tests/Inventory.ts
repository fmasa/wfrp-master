import {suite, test} from "mocha-typescript";
import {CharacterSubCollectionSuite} from "./CharacterSubCollectionSuite";
import {uuid} from "uuidv4";
import * as firebase from "@firebase/testing";
import {withoutField} from "./utils";
import {CollectionReference, Firestore} from "./firebase";

@suite
class Inventory extends CharacterSubCollectionSuite {
    private inventoryItem = {
        id: uuid(),
        quantity: 1,
        name: "Sword of Chaos Champion",
        description: "Trust me, you don't want to show it to people",
        encumbrance: 10.15,
    };

    private inventoryItems(app: Firestore, userId: string): CollectionReference
    {
        return app.collection("parties")
            .doc(this.partyId)
            .collection("characters")
            .doc(userId)
            .collection("inventory");
    }

    @test
    async "user (and GM) can add item to his inventory"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const items = this.inventoryItems(this.authedApp(userId), this.userId1);

            await firebase.assertSucceeds(items.doc(this.inventoryItem.id).set(this.inventoryItem));

            await firebase.assertSucceeds(items.doc(this.inventoryItem.id).set(withoutField(this.inventoryItem, "encumbrance")));
        }
    }

    @test
    async "other users CANNOT add item to character's inventory"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            const items = this.inventoryItems(this.authedApp(userId), this.userId1);

            await firebase.assertFails(items.doc(this.inventoryItem.id).set(this.inventoryItem));
        }
    }

    @test
    async "user (and GM) can update item in his inventory"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const items = this.inventoryItems(this.authedApp(userId), this.userId1);
            await items.doc(this.inventoryItem.id).set(this.inventoryItem);

            await firebase.assertSucceeds(items.doc(this.inventoryItem.id).set({quantity: 100}, {merge: true}));
        }
    }

    @test
    async "other users CANNOT update item in character's inventory"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.inventoryItems(this.authedApp(this.userId1), this.userId1).doc(this.inventoryItem.id).set(this.inventoryItem);
            const items = this.inventoryItems(this.authedApp(userId), this.userId1);

            await firebase.assertFails(items.doc(this.inventoryItem.id).set({quantity: 100}, {merge: true}));
        }
    }

    @test
    async "user (and GM) can remove item from his inventory"() {
        for (const userId of [this.userId1, this.gameMasterId]) {
            const items = this.inventoryItems(this.authedApp(userId), this.userId1);
            await items.doc(this.inventoryItem.id).set(this.inventoryItem);

            await firebase.assertSucceeds(items.doc(this.inventoryItem.id).delete());
        }
    }

    @test
    async "other users CANNOT remove item from character's inventory"() {
        for (const userId of [this.userId2, 'user-not-in-party']) {
            await this.inventoryItems(this.authedApp(this.userId1), this.userId1).doc(this.inventoryItem.id).set(this.inventoryItem);
            const items = this.inventoryItems(this.authedApp(userId), this.userId1);

            await firebase.assertFails(items.doc(this.inventoryItem.id).delete());
        }
    }

    @test
    async "all party members can read each other's inventory"() {
        for (const userId of [this.userId1, this.userId2, this.gameMasterId]) {
            await this.inventoryItems(this.authedApp(userId), this.userId1).get();
        }
    }

    @test
    async "inventory item with field missing CANNOT be saved"() {
        const items = this.inventoryItems(this.authedApp(this.userId1), this.userId1);

        await Promise.all(Object.keys(this.inventoryItem).map(field => {
            // For BC, TODO: remove in future version
            if (field === "encumbrance") {
                return Promise.resolve();
            }

            const item = {...this.inventoryItem};
            const itemId = item.id;

            delete item[field];

            return firebase.assertFails(items.doc(itemId).set(item));
        }));
    }

    @test
    async "item with invalid field CANNOT be saved"() {
        const itemDoc = this.inventoryItems(this.authedApp(this.userId1), this.userId1).doc(this.inventoryItem.id);

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

                // Name with nothing but whitespaces
                {name: " \t\r"},

                // Description too long
                {description: "a".repeat(2501)},

                // Zero quantity
                {quantity: 0},

                // Negative quantity
                {quantity: -1}
            ].map(doc => firebase.assertFails(itemDoc.set({...this.inventoryItem, ...doc})))
        );

        await Promise.all(Object.keys(this.inventoryItem).map(field => {
            return firebase.assertFails(
                itemDoc.set({
                    ...this.inventoryItem,
                    [field]: typeof this.inventoryItem[field] == 'string' ? true : 'foo',
                })
            );
        }));
    }
}
