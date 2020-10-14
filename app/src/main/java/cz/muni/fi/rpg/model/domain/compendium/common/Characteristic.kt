package cz.muni.fi.rpg.model.domain.compendium.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Stats

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

    @StringRes
    fun getShortcutNameId(): Int {
        return when (this) {
            AGILITY -> R.string.label_shortcut_agility
            BALLISTIC_SKILL -> R.string.label_shortcut_ballistic_skill
            DEXTERITY -> R.string.label_shortcut_dexterity
            INITIATIVE -> R.string.label_shortcut_initiative
            INTELLIGENCE -> R.string.label_shortcut_intelligence
            FELLOWSHIP -> R.string.label_shortcut_fellowship
            STRENGTH -> R.string.label_shortcut_strength
            TOUGHNESS -> R.string.label_shortcut_toughness
            WEAPON_SKILL -> R.string.label_shortcut_weapon_skill
            WILL_POWER -> R.string.label_shortcut_will_power
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
        INTELLIGENCE -> R.drawable.ic_intelligence
        FELLOWSHIP -> R.drawable.ic_fellowship
        STRENGTH -> R.drawable.ic_strength
        TOUGHNESS -> R.drawable.ic_toughness
        WEAPON_SKILL -> R.drawable.ic_weapon_skill
        WILL_POWER -> R.drawable.ic_will_power
    }
}