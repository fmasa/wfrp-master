import * as t from "io-ts";
import {isLeft} from "fp-ts/lib/Either";
import {hasAccessToCharacter} from "./acl";
import {firestore} from "firebase-admin";

import {onCall} from "firebase-functions/v2/https";

const RequiredFields = t.type({
    partyId: t.string,
    characterId: t.string,
});

type RequiredProps = (typeof RequiredFields)["props"];
type RequestBody<T> = T extends t.TypeC<any>
    ? (T["props"] extends RequiredProps ? T : never)
    : never;

export const characterChange = <T, R>(
    requestBodyCodec: RequestBody<T>,
    handler: (
        body: t.TypeOf<RequestBody<T>>,
        character: firestore.DocumentReference
    ) => Promise<R>,
) => {
    return onCall(async (request) => {
        const body = requestBodyCodec.decode(request.data);

        if (isLeft(body)) {
            return {
                status: "error",
                error: 400,
                message: "Invalid request body",
            };
        }

        const userId = request.auth?.uid;
        const {characterId, partyId} = body.right;

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

        const character = firestore().doc(`parties/${partyId}/characters/${characterId}`);

        if (!(await character.get()).exists) {
            return {
                status: "error",
                error: 404,
                message: "Character does not exist",
            };
        }

        return handler(body.right, character);
    });
};
