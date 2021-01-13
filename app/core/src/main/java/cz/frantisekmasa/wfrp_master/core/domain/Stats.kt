package cz.frantisekmasa.wfrp_master.core.domain

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
            ).all { it >= 0 }
        )
    }

    val agilityBonus: Int get() = agility / 10
    val toughnessBonus: Int get() = toughness / 10
    val initiativeBonus: Int get() = initiative / 10

    operator fun plus(other: Stats) = Stats(
        weaponSkill = weaponSkill + other.weaponSkill,
        dexterity = dexterity + other.dexterity,
        ballisticSkill = ballisticSkill + other.ballisticSkill,
        strength = strength + other.strength,
        toughness = toughness + other.toughness,
        agility = agility + other.agility,
        intelligence = intelligence + other.intelligence,
        initiative = initiative + other.initiative,
        willPower = willPower + other.willPower,
        fellowship = fellowship + other.fellowship
    )
}
