import {firestore} from 'firebase-admin';
import * as t from "io-ts";
import {characterChange} from "../characterChange";
import { v4 as uuidv4 } from "uuid";

const RequestBody = t.interface({
    partyId: t.string,
    characterId: t.string,
    newName: t.string,
});

export const duplicateCharacter = characterChange(RequestBody, async (body, character) => {
    const batch = firestore().batch();
    const newCharacterId = uuidv4();
    const newCharacter = character.parent.doc(newCharacterId);
    const characterData = (await character.get()).data();

    batch.set(
        newCharacter,
        {
            ...characterData,
            id: newCharacterId,
            name: body.newName,
        }
    )

    const collections = await character.listCollections();

    await Promise.all(
        collections.map(async (collection) => {
            await Promise.all(
                (await collection.listDocuments()).map(async (document) => {
                    batch.set(
                        newCharacter.collection(collection.id).doc(document.id),
                        (await document.get()).data(),
                    );
                })
            );
        })
    );

    await batch.commit();

    return {
        status: "success",
    };
})
