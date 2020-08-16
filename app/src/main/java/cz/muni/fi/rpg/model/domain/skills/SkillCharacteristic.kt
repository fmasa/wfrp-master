package cz.muni.fi.rpg.model.domain.skills

import androidx.annotation.StringRes
import cz.muni.fi.rpg.R

enum class SkillCharacteristic {
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
}