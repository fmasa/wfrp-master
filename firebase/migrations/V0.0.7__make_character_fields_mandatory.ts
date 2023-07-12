export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const characters = await firestore.collectionGroup("characters").get();

    for (const document of characters.docs) {
        if (!("archived" in document.data())) {
            await document.ref.set({archived: false}, {merge: true});
        }

        if (!("type" in document.data())) {
            await document.ref.set({type: "PLAYER_CHARACTER"}, {merge: true});
        }

        if (!("publicName" in document.data())) {
            await document.ref.set({publicName: null}, {merge: true});
        }

        if (!("encumbranceBonus" in document.data())) {
            await document.ref.set({encumbranceBonus: 0}, {merge: true});
        }

        if (!("compendiumCareer" in document.data())) {
            await document.ref.set({compendiumCareer: null}, {merge: true});
        }

        if (!("avatarUrl" in document.data())) {
            await document.ref.set({avatarUrl: null}, {merge: true});
        }

        if (!("conditions" in document.data())) {
            await document.ref.set({conditions: {conditions: {}}}, {merge: true});
        }

        if (!("status" in document.data())) {
            await document.ref.set({status: {tier: "BRASS", standing: 0}}, {merge: true});
        }

        if (!("hiddenTabs" in document.data())) {
            await document.ref.set({hiddenTabs: []}, {merge: true});
        }

        if (!("size" in document.data())) {
            await document.ref.set({size: null}, {merge: true});
        }

        if (!("woundsModifiers" in document.data())) {
            await document.ref.set(
                {
                    woundsModifiers: {
                        afterMultiplier: 1,
                        extraToughnessBonusMultiplier: 0,
                        isConstruct: false,
                    }
                },
                {merge: true}
            );
        }
    }
}
