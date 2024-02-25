import {storage} from 'firebase-admin';
import {file} from "tmp-promise";
import * as sharp from "sharp";
import * as t from "io-ts";
import {characterChange} from "../characterChange";
import {generateAvatarUrl, getAvatarPath, METADATA} from "../avatar";

const imageSize = 500;

const RequestBody = t.type({
    partyId: t.string,
    characterId: t.string,
    imageData: t.string,
});

export const changeCharacterAvatar = characterChange(RequestBody,async (body, character) => {
    const partyId = body.partyId;
    const characterId = body.characterId;
    const imageData = body.imageData;

    const tempFile = await file();

    await (await cropToRectangle(sharp(Buffer.from(imageData, "base64"))))
        .resize(imageSize, imageSize, {fit: "outside"})
        .webp()
        .toFile(tempFile.path);

    const bucket = storage().bucket();
    const response = await bucket.upload(
        tempFile.path,
        {
            destination: getAvatarPath(partyId, characterId),
            metadata: METADATA,
        }
    );

    const url = await generateAvatarUrl(response, bucket);

    console.debug(`File url: ${url}`);

    await character.update("avatarUrl", url);
    await tempFile.cleanup();

    return {
        status: "success",
        url,
    };
});

const cropToRectangle = async (image: sharp.Sharp): Promise<sharp.Sharp> => {
    const metadata = await image.metadata();
    const width = metadata.width;
    const height = metadata.height;

    if (width === undefined || height === undefined) {
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
