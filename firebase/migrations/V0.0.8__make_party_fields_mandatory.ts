export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const parties = await firestore.collection("parties").get();

    for (const document of parties.docs) {
        if (!("activeCombat" in document.data())) {
            await document.ref.set({activeCombat: null}, {merge: true});
        }

        if (!("settings" in document.data())) {
            await document.ref.set(
                {
                    settings: {
                        initiativeStrategy: "INITIATIVE_CHARACTERISTIC",
                        advantageSystem: "INITIATIVE_CHARACTERISTIC",
                        advantageCap: ""
                    }
                },
                {merge: true},
            );
        }

        if (!("time" in document.data())) {
            await document.ref.set(
                {
                    time: {
                        imperialDay: 1004400,
                        minutes: 720,
                    }
                },
                {merge: true},
            );
        }
    }
}
