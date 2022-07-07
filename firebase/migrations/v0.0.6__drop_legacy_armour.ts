import { uuid } from 'uuidv4';

const ARMOUR_PARTS = {
      "leftLeg": "LEFT_LEG",
      "head": "HEAD",
      "rightArm": "RIGHT_ARM",
      "body": "BODY",
      "rightLeg": "RIGHT_LEG",
      "leftArm": "LEFT_ARM",
      "shield": "SHIELD",
};

export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const characters = await firestore.collection("parties").doc("bbc08a6e-38dd-48de-8608-0e5461c5d190")
    .collection("characters").get();
    //const characters = await firestore.collectionGroup("characters").get();

    for (const document of characters.docs) {
        const character = document.data();

        const partyId = document.ref.parent.parent.id;

        const armourDoc = document.ref.collection("features").doc("armor");

        const armour = (await armourDoc.get()).data();

        if (armour == undefined) {
            continue;
        }

        const newArmourData = {};

        for (const [key, value] of Object.entries(armour)) {
            newArmourData[key] = 0;

            if (value == 0) {
                continue;
            }

            const part = ARMOUR_PARTS[key];

            if (!part) {
                throw `Unknown armour part ${part}`;
            }

            await createArmourTrapping(document.ref, part, value);
        }

        await armourDoc.set(newArmourData);
    }
}

async function createArmourTrapping(
    character: firebase.firestore.DocumentReference,
    armourPart: string,
    points: number
): Promise<void> {
    const id = uuid();

    await character.collection("inventory")
        .doc(id)
        .set({
            id: id,
            name: "Migrated " + armourPart.replace("_", " ").toLowerCase() + " armour",
            encumbrance: 0,
            containerId: null,
            description: "This armour was automatically created from old Armour mechanism unrelated to trappings.\n" +
                         "From now on, equipped armour is automatically calculated from equipped Armour trappings",
            quantity: 1,
            trappingType: {
                kind: "ARMOUR",
                flaws: {},
                locations: [armourPart],
                points: points,
                qualities: {},
                type: "OTHER",
                worn: true,
            },
        });
}