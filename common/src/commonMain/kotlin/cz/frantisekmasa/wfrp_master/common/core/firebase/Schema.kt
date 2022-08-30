package cz.frantisekmasa.wfrp_master.common.core.firebase

object Schema {
    const val Parties = "parties"
    const val Characters = "characters"
    const val CharacterFeatures = "features"

    object Party {
        const val Encounters = "encounters"

        object Encounter {
            // Tech debt. TODO: Rename to "npcs"
            const val Npcs = "combatants"
        }
    }

    object Character {
        const val Blessings = "blessings"
        const val InventoryItems = "inventory"
        const val Miracles = "miracles"
        const val Skills = "skills"
        const val Spells = "spells"
        const val Talents = "talents"
    }

    object Compendium {
        const val Blessings = "blessings"
        const val Miracles = "miracles"
        const val Skills = "skills"
        const val Spells = "spells"
        const val Talents = "talents"
        const val Traits = "traits"
    }
}