package cz.frantisekmasa.wfrp_master.common.core.firebase

object Schema {
    const val PARTIES = "parties"
    const val CHARACTERS = "characters"

    object Party {
        const val ENCOUNTERS = "encounters"
    }

    object Character {
        const val BLESSINGS = "blessings"
        const val INVENTORY_ITEMS = "inventory"
        const val MIRACLES = "miracles"
        const val SKILLS = "skills"
        const val SPELLS = "spells"
        const val TALENTS = "talents"
        const val TRAITS = "traits"
    }

    object Compendium {
        const val BLESSINGS = "blessings"
        const val MIRACLES = "miracles"
        const val SKILLS = "skills"
        const val SPELLS = "spells"
        const val TALENTS = "talents"
        const val TRAITS = "traits"
        const val TRAPPINGS = "trappings"
        const val CAREERS = "careers"
        const val JOURNAL = "journal"
    }
}
