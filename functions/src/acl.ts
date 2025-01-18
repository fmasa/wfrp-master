import {firestore} from "firebase-admin";

type Party = { gameMasterId: string, users: string[] } | { gameMasterId: null, users: [string] }

interface Character {
    userId: string | null;
    image?: string | null;
}

export const hasAccessToCharacter = async (
    userId: string,
    partyId: string,
    characterId: string
): Promise<boolean> => {
    const character = isCharacter(userId, partyId, characterId);
    const gm = isGmOfParty(userId, partyId);

    return (await character) || (await gm);
};

const isCharacter = async (
    userId: string,
    partyId: string,
    characterId: string
): Promise<boolean> => {
    const character = await getCharacter(partyId, characterId);

    return character?.userId === userId;
};

const getCharacter = async (
    partyId: string,
    characterId: string
): Promise<Character | undefined> => {
    return (
        await firestore()
            .doc(`parties/${partyId}/characters/${characterId}`)
            .get()
    ).data() as Character | undefined;
};

const isGmOfParty = async (
    userId: string,
    partyId: string
): Promise<boolean> => {
    const party = (await firestore().doc(`parties/${partyId}`).get()).data() as Party;

    return party?.gameMasterId === userId ||
        (party?.gameMasterId === null && party?.users[0] === userId);
};
