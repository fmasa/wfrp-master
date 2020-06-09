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
    val fellowship: Int,
    val magic: Int
) {
    val strengthBonus: Int
        get() = strength / 10

    val toughnessBonus: Int
        get() = toughness / 10

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
                fellowship,
                magic
            ).all { it in 0..100 }
        )
    }
}