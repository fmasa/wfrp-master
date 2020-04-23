package cz.muni.fi.rpg.model.domain.character

data class Stats(
    val weaponSkill: Int,
    val ballisticSkill: Int,
    val strength: Int,
    val toughness: Int,
    val agility: Int,
    val intelligence: Int,
    val willPower: Int,
    val fellowship: Int
) {
    init {
        require(
            listOf(
                weaponSkill,
                ballisticSkill,
                strength,
                toughness,
                agility,
                intelligence,
                willPower,
                fellowship
            ).all { it in 0..100 }
        )
    }
}