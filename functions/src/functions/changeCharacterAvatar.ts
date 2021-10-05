import * as functions from "firebase-functions";
import {firestore, storage} from 'firebase-admin';
import {isLeft} from "fp-ts/Either";
import {hasAccessToCharacter} from "../acl";
import {file} from "tmp-promise";
import * as sharp from "sharp";
import * as t from "io-ts";
import {UploadResponse} from "@google-cloud/storage/build/src/bucket";
import {Bucket} from "@google-cloud/storage";
import {Sharp} from "sharp";

const imageSize = 500

const RequestBody = t.interface({
    partyId: t.string,
    characterId: t.string,
    imageData: t.string,
});

export const changeCharacterAvatar = functions.https.onCall(async (data, context) => {
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
    const imageData = body.right.imageData;

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

    const tempFile = await file();

    await (await cropToRectangle(sharp(Buffer.from(imageData, "base64"))))
        .resize(imageSize, imageSize, {fit: "outside"})
        .webp()
        .toFile(tempFile.path);

    const bucket = storage().bucket();
    const response = await bucket.upload(
        tempFile.path,
        {
            destination: `images/parties/${partyId}/characters/${characterId}.webp`,
            metadata: {
                contentType: "image/webp",
                cacheControl: `max-age=${365 * 24 * 60 * 60}`,
            },
        }
    );

    const url = generateAvatarUrl(response, bucket);

    console.debug(`File url: ${url}`);

    await firestore().doc(`parties/${partyId}/characters/${characterId}`)
        .update("avatarUrl", url)

    await tempFile.cleanup();

    return {
        status: "success",
        url,
    };
});

const cropToRectangle = async (image: Sharp): Promise<Sharp> => {
    const metadata = await image.metadata();
    const width = metadata.width;
    const height = metadata.height;

    if (width == undefined || height == undefined) {
        console.error("Could not read image width and height");

        return image;
    }

    const size = Math.min(width, height);

    return image.extract({
        top: Math.round((height - size) / 2),
        left: Math.round((width - size) / 2),
        width: size,
        height: size,
    })
}

const generateAvatarUrl = (response: UploadResponse, bucket: Bucket): string => {
    if ("FIREBASE_STORAGE_EMULATOR_HOST" in process.env) {
        return response[1].mediaLink;
    }

    return "https://firebasestorage.googleapis.com/v0/b/"
        + bucket.name
        + "/o/"
        + encodeURIComponent(response[0].name)
        + "?alt=media"
        + "&v="
        + (+new Date());
}
