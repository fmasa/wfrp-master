package cz.muni.fi.rpg.model.domain.skills

import androidx.annotation.StringRes
import cz.muni.fi.rpg.R

enum class SkillCharacteristic {
    AGILITY,
    DEXTERITY,
    INTELLIGENCE,
    FELLOWSHIP,
    STRENGTH,
    TOUGHNESS,
    WILL_POWER;

    @StringRes
    fun getReadableNameId(): Int {
        return when (this) {
            AGILITY -> R.string.label_agility
            DEXTERITY -> R.string.label_dexterity
            INTELLIGENCE -> R.string.label_intelligence
            FELLOWSHIP -> R.string.label_fellowship
            STRENGTH -> R.string.label_strength
            TOUGHNESS -> R.string.label_toughness
            WILL_POWER -> R.string.label_will_power
        }
    }
}