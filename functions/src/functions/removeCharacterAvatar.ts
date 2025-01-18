import * as t from "io-ts";
import {getStorage} from "firebase-admin/storage";
import {characterChange} from "../characterChange";

const RequestBody = t.interface({
    partyId: t.string,
    characterId: t.string,
});

export const removeCharacterAvatar = characterChange(RequestBody, async (body, character) => {
    const {partyId, characterId} = body;

    await getStorage()
        .bucket()
        .deleteFiles({prefix: `images/parties/${partyId}/characters/${characterId}.webp`});

    await character.update("avatarUrl", null);

    return {status: "success"};
});
