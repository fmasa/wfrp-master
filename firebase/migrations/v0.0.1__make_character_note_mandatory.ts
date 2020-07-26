import firebase from "firebase";

export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const characters = await firestore.collectionGroup("characters").get();

    for (const document of characters.docs) {
        if (!("note" in document.data())) {
            await document.ref.set({note: ""}, {merge: true});
        }
    }
}
