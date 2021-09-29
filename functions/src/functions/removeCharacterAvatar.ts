import * as functions from "firebase-functions";
import {isLeft} from "fp-ts/Either";
import {hasAccessToCharacter} from "../acl";
import * as t from "io-ts";
import {firestore, storage} from "firebase-admin";

const RequestBody = t.interface({
    partyId: t.string,
    characterId: t.string,
});

export const removeCharacterAvatar = functions.https.onCall(async (data, context) => {
    const body = RequestBody.decode(data);

    if (isLeft(body)) {
        return {
            error: 400,
            message: "Invalid request body",
        };
    }

    const userId = context.auth?.uid;
    const partyId = body.right.partyId;
    const characterId = body.right.characterId;

    if (userId === undefined) {
        return {
            status: "error",
            error: 401,
            message: "User is not authorized",
        };
    }

    if (!await hasAccessToCharacter(userId, partyId, characterId)) {
        return {
            status: "error",
            error: 403,
            message: "User does not have access to given character",
        };
    }

    await storage()
        .bucket()
        .deleteFiles({prefix: `images/parties/${partyId}/characters/${characterId}.webp`});

    await firestore().doc(`parties/${partyId}/characters/${characterId}`)
        .update("avatarUrl", null)

    return {status: "success"};
});
