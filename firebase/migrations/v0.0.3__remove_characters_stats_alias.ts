import firebase from "firebase";

export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const characters = await firestore.collectionGroup("characters").get();

    for (const document of characters.docs) {
        const character = document.data();

        if ("stats" in character) {
            character["characteristicsBase"] = character["stats"];
            delete character["stats"];

            if (! ("characteristicsAdvances" in character)) {
                character["characteristicsAdvances"] = {
                    weaponSkill: 0,
                    dexterity: 0,
                    initiative: 0,
                    ballisticSkill: 0,
                    strength: 0,
                    toughness: 0,
                    agility: 0,
                    intelligence: 0,
                    willPower: 0,
                    fellowship: 0,
                }
            }

            delete character["maxStats"];
            await document.ref.set(character);
        }
    }
}
