import type {Storage} from "firebase-admin/storage";

type Bucket = ReturnType<Storage["bucket"]>;
type File = ReturnType<Bucket["file"]>;

export const generateAvatarUrl = async (file: File, bucket: Bucket): Promise<string> => {
    if ("FIREBASE_STORAGE_EMULATOR_HOST" in process.env) {
        const [metadata] = await file.getMetadata();

        const {mediaLink} = metadata;

        if (!mediaLink) {
            throw new Error("Expected media link in emulated Firebase storage");
        }

        return mediaLink;
    }

    return "https://firebasestorage.googleapis.com/v0/b/" +
        bucket.name +
        "/o/" +
        encodeURIComponent(file.name) +
        "?alt=media" +
        "&v=" +
        (+new Date());
};

export const getAvatarPath = (
    partyId: string,
    characterId: string
): string => `images/parties/${partyId}/characters/${characterId}.webp`;

export const METADATA = {
    contentType: "image/webp",
    cacheControl: `max-age=${365 * 24 * 60 * 60}`,
};
