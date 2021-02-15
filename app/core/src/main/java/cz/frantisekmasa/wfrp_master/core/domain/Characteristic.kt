package cz.frantisekmasa.wfrp_master.core.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import cz.frantisekmasa.wfrp_master.core.R

enum class Characteristic {
    AGILITY,
    BALLISTIC_SKILL,
    DEXTERITY,
    INITIATIVE,
    INTELLIGENCE,
    FELLOWSHIP,
    STRENGTH,
    TOUGHNESS,
    WEAPON_SKILL,
    WILL_POWER;

    fun getShortcutName(): String {
        return when (this) {
            AGILITY -> "Ag"
            BALLISTIC_SKILL -> "BS"
            DEXTERITY -> "Dex"
            INITIATIVE -> "I"
            INTELLIGENCE -> "Int"
            FELLOWSHIP -> "Fel"
            STRENGTH -> "S"
            TOUGHNESS -> "T"
            WEAPON_SKILL -> "WS"
            WILL_POWER -> "WP"
        }
    }

    fun characteristicValue(characteristics: Stats): Int = when (this) {
        AGILITY -> characteristics.agility
        BALLISTIC_SKILL -> characteristics.ballisticSkill
        DEXTERITY -> characteristics.dexterity
        FELLOWSHIP -> characteristics.fellowship
        INITIATIVE -> characteristics.initiative
        INTELLIGENCE -> characteristics.intelligence
        STRENGTH -> characteristics.strength
        TOUGHNESS -> characteristics.toughness
        WEAPON_SKILL -> characteristics.weaponSkill
        WILL_POWER -> characteristics.willPower
    }

    @DrawableRes
    fun getIconId(): Int = when (this) {
        AGILITY -> R.drawable.ic_agility
        BALLISTIC_SKILL -> R.drawable.ic_ballistic_skill
        DEXTERITY -> R.drawable.ic_dexterity
        INITIATIVE -> R.drawable.ic_initiative
        INTELLIGENCE -> R.drawable.ic_brain
        FELLOWSHIP -> R.drawable.ic_fellowship
        STRENGTH -> R.drawable.ic_strength
        TOUGHNESS -> R.drawable.ic_toughness
        WEAPON_SKILL -> R.drawable.ic_weapon_skill
        WILL_POWER -> R.drawable.ic_will_power
    }

    @StringRes
    fun getNameId(): Int = when(this) {
        AGILITY -> R.string.label_agility
        BALLISTIC_SKILL -> R.string.label_ballistic_skill
        DEXTERITY -> R.string.label_dexterity
        INITIATIVE -> R.string.label_initiative
        INTELLIGENCE -> R.string.label_intelligence
        FELLOWSHIP -> R.string.label_fellowship
        STRENGTH -> R.string.label_strength
        TOUGHNESS -> R.string.label_toughness
        WEAPON_SKILL -> R.string.label_weapon_skill
        WILL_POWER -> R.string.label_will_power
    }
}