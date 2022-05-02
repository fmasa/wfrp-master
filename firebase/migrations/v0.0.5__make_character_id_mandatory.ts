import firebase from "firebase";

export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const characters = await firestore.collectionGroup("characters").get();

    for (const document of characters.docs) {
        const character = document.data();

        if (! ("id" in character)) {
            await document.ref.set({id: document.id}, {merge: true});
        }
    }
}
