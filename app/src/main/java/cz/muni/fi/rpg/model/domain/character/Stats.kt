package cz.muni.fi.rpg.model.domain.character

data class Stats(
    val weaponSkill: Int,
    val dexterity: Int,
    val ballisticSkill: Int,
    val strength: Int,
    val toughness: Int,
    val agility: Int,
    val intelligence: Int,
    val initiative: Int,
    val willPower: Int,
    val fellowship: Int
) {
    init {
        require(
            listOf(
                dexterity,
                weaponSkill,
                ballisticSkill,
                strength,
                toughness,
                agility,
                initiative,
                intelligence,
                willPower,
                fellowship
            ).all { it in 0..100 }
        )
    }

    fun allLowerOrEqualTo(other: Stats): Boolean {
        return weaponSkill <= other.weaponSkill
                && dexterity <= other.dexterity
                && ballisticSkill <= other.ballisticSkill
                && strength <= other.strength
                && toughness <= other.toughness
                && agility <= other.agility
                && intelligence <= other.intelligence
                && initiative <= other.initiative
                && willPower <= other.willPower
                && fellowship <= other.fellowship
    }
}
