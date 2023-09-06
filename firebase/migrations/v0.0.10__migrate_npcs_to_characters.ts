export async function migrate({firestore}: { firestore: firebase.firestore.Firestore }): Promise<void> {
    const firestore = getFirestore();

    const allNpcs = await firestore.collectionGroup("combatants").get();

    // We will create a new NPC Character for each NPC.
    // The Character will have same ID as original NPC.
    // Then we remove the NPC altogether
    for (const npc of allNpcs.docs) {
        const encounterDoc = npc.ref.parent.parent;
        const encounter = await encounterDoc.get();

        const npcData = npc.data();

        if (!encounter.exists) {
            // I have checked that no such NPCs are in an active combat
            console.log("Encounter does not exist");
            await npc.ref.delete();

            continue;
        }

        const partyDoc = encounterDoc.parent.parent;
        const encounterData = encounter.data();

        const armour = Object.keys(npcData.armor)
            .filter(key => npcData.armor[key] > 0)
            .map(key => {
                const part = key.replace(/[A-Z]/g, letter => ` ${letter.toLowerCase()}`)

                return `${part} - ${npcData.armor[key]}`;
            })

        const character = {
            id: npcData.id,
            type: "NPC",
            name: npcData.name,
            publicName: null,
            userId: null,
            career: "",
            compendiumCareer: null,
            socialClass: "",
            status: {
                tier: "BRASS",
                standing: 0,
            },
            psychology: "",
            motivation: "",
            race: null,
            characteristicsBase: npcData.stats,
            characteristicsAdvances: {
                weaponSkill: 0,
                dexterity: 0,
                ballisticSkill: 0,
                strength: 0,
                toughness: 0,
                agility: 0,
                intelligence: 0,
                initiative: 0,
                willPower: 0,
                fellowship: 0,
            },
            points: {
                corruption: 0,
                fate: 0,
                fortune: 0,
                wounds: npcData.wounds.current,
                maxWounds: npcData.wounds.max,
                resilience: 0,
                resolve: 0,
                sin: 0,
                experience: 0,
                spentExperience: 0,
            },
            ambitions: {
                shortTerm: "",
                longTerm: "",
            },
            conditions: ("conditions" in npcData) ? npcData.conditions : {conditions: {}},
            mutation: "",
            note: "Automatically migrated from NPC" + (armour.length > 0 ? `\n\n**Armour:**\n${armour.join('\n')}` : ""),
            hardyTalent: false,
            woundsModifiers: {
                afterMultiplier: 1,
                extraToughnessBonusMultiplier: 0,
                isConstruct: false,
            },
            encumbranceBonus: 0,
            archived: false,
            avatarUrl: null,
            money: {pennies: 0},
            hiddenTabs: [],
            size: null,
        }

        await partyDoc.collection("characters")
            .doc(npcData.id)
            .set(character);

        await npc.ref.delete();
    }

    const partiesWithActiveCombat = await firestore.collection("parties").where("activeCombat", "!=", null)
        .get()

    // NPCs may be combatants in active combats
    // We will replace them by newly created NPC Characters
    for (const party of partiesWithActiveCombat.docs) {
        const partyData = party.data();
        const combat = partyData.activeCombat;

        if (!combat.combatants.some(combatant => combatant['@type'] == 'npc')) {
            continue;
        }

        await party.ref.update(
            {
                ...partyData,
                activeCombat: {
                    ...combat,
                    combatants: combat.combatants.map(combatant => {
                        if (combatant["@type"] != 'npc') {
                            return combatant;
                        }

                        const newCombatant = {
                            ...combatant,
                            '@type': 'character',
                            'characterId': combatant.npcId.npcId,
                        };
                        delete newCombatant['npcId'];

                        return newCombatant;
                    })
                }
            }
        )
    }
}