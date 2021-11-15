package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.localization.Strings

enum class Characteristic(override val nameResolver: (strings: Strings) -> String) : NamedEnum {
    AGILITY({ it.characteristicAgility }),
    BALLISTIC_SKILL({ it.characteristicBallisticSkill }),
    DEXTERITY({ it.characteristicDexterity }),
    INITIATIVE({ it.characteristicFellowship }),
    INTELLIGENCE({ it.characteristicInitiative }),
    FELLOWSHIP({ it.characteristicIntelligence }),
    STRENGTH({ it.characteristicStrength }),
    TOUGHNESS({ it.characteristicToughness }),
    WEAPON_SKILL({ it.characteristicWeaponSkill }),
    WILL_POWER({ it.characteristicWillPower });

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

    @Composable
    fun getIcon(): Resources.Drawable = when(this) {
        AGILITY -> Resources.Drawable.Agility
        BALLISTIC_SKILL -> Resources.Drawable.BallisticSkill
        DEXTERITY -> Resources.Drawable.Dexterity
        INITIATIVE -> Resources.Drawable.Initiative
        INTELLIGENCE -> Resources.Drawable.Intelligence
        FELLOWSHIP -> Resources.Drawable.Fellowship
        STRENGTH -> Resources.Drawable.Strength
        TOUGHNESS -> Resources.Drawable.Toughness
        WEAPON_SKILL -> Resources.Drawable.WeaponSkill
        WILL_POWER -> Resources.Drawable.WillPower
    }
}
