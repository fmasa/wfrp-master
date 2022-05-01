package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
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
    val strengthBonus: Int get() = strength / 10
    val toughnessBonus: Int get() = toughness / 10
    val initiativeBonus: Int get() = initiative / 10
    val willPowerBonus: Int get() = willPower / 10

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

    fun get(characteristic: Characteristic): Int = when (characteristic) {
        Characteristic.AGILITY -> agility
        Characteristic.BALLISTIC_SKILL -> ballisticSkill
        Characteristic.DEXTERITY -> dexterity
        Characteristic.FELLOWSHIP -> fellowship
        Characteristic.INITIATIVE -> initiative
        Characteristic.INTELLIGENCE -> intelligence
        Characteristic.STRENGTH -> strength
        Characteristic.TOUGHNESS -> toughness
        Characteristic.WEAPON_SKILL -> weaponSkill
        Characteristic.WILL_POWER -> willPower
    }
}
