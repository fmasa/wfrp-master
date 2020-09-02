import firebase from "firebase";

export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const parties = await firestore.collection("parties").get();

    for (const document of parties.docs) {
        if (!("archived" in document.data())) {
            await document.ref.set({archived: false}, {merge: true});
        }
    }
}
