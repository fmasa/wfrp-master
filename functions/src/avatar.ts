import {Bucket, CopyResponse, UploadResponse} from "@google-cloud/storage";

export const generateAvatarUrl = async (response: UploadResponse|CopyResponse, bucket: Bucket): Promise<string> => {
    if ("FIREBASE_STORAGE_EMULATOR_HOST" in process.env) {
        const [metadata] = await response[0].getMetadata();

        return metadata.mediaLink;
    }

    return "https://firebasestorage.googleapis.com/v0/b/"
        + bucket.name
        + "/o/"
        + encodeURIComponent(response[0].name)
        + "?alt=media"
        + "&v="
        + (+new Date());
}

export const getAvatarPath = (partyId: string, characterId: string): string => `images/parties/${partyId}/characters/${characterId}.webp`;

export const METADATA = {
    contentType: "image/webp",
    cacheControl: `max-age=${365 * 24 * 60 * 60}`,
};
