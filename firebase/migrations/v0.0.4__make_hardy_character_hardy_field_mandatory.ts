import firebase from "firebase";

export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const characters = await firestore.collectionGroup("characters").get();

    for (const document of characters.docs) {
        const character = document.data();

        if (! ("hardyTalent" in character)) {
            await document.ref.set({
                hardyTalent: false,
                points: {...character.points, hardyWoundsBonus: 0}
            }, {merge: true});
        }
    }
}
