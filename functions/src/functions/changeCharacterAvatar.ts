import {storage} from "firebase-admin";
import sharp from "sharp";
import * as t from "io-ts";
import {characterChange} from "../characterChange";
import {generateAvatarUrl, getAvatarPath, METADATA} from "../avatar";

const imageSize = 500;

const RequestBody = t.type({
    partyId: t.string,
    characterId: t.string,
    imageData: t.string,
});

export const changeCharacterAvatar = characterChange(RequestBody, async (body, character) => {
    const partyId = body.partyId;
    const characterId = body.characterId;
    const imageData = body.imageData;

    const avatar = await (await cropToRectangle(sharp(Buffer.from(imageData, "base64"))))
        .resize(imageSize, imageSize, {fit: "outside"})
        .webp()
        .toBuffer();

    const bucket = storage().bucket();
    const file = bucket.file(getAvatarPath(partyId, characterId));

    await file.save(avatar, {metadata: METADATA});
    const url = await generateAvatarUrl(file, bucket);

    console.debug(`File url: ${url}`);

    await character.update("avatarUrl", url);

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
    });
};
