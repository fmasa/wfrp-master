package cz.frantisekmasa.wfrp_master.common.core.firebase

object Schema {
    const val Parties = "parties"
    const val Characters = "characters"

    object Party {
        const val Encounters = "encounters"
    }

    object Character {
        const val Blessings = "blessings"
        const val InventoryItems = "inventory"
        const val Miracles = "miracles"
        const val Skills = "skills"
        const val Spells = "spells"
        const val Talents = "talents"
        const val Traits = "traits"
    }

    object Compendium {
        const val Blessings = "blessings"
        const val Miracles = "miracles"
        const val Skills = "skills"
        const val Spells = "spells"
        const val Talents = "talents"
        const val Traits = "traits"
        const val Trappings = "trappings"
        const val Careers = "careers"
    }
}
