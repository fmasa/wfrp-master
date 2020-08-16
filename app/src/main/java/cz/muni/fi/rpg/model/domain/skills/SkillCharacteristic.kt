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
    fun getReadableNameId(): Int {
        return when (this) {
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
}