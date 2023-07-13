export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const parties = await firestore.collection("parties").get();

    for (const document of parties.docs) {
        const data = document.data();

        if (data.settings.advantageSystem == "INITIATIVE_CHARACTERISTIC") {
            await document.ref.set(
                {
                    settings: {
                        initiativeStrategy: data.settings.initiativeStrategy,
                        advantageSystem: "CORE_RULEBOOK",
                        advantageCap: data.settings.advantageCap
                    }
                },
                {merge: true},
            );
        }
    }
}