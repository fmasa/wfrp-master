package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Immutable
enum class Characteristic(override val nameResolver: (strings: Strings) -> String) : NamedEnum {
    AGILITY({ it.characteristics.agility }),
    BALLISTIC_SKILL({ it.characteristics.ballisticSkill }),
    DEXTERITY({ it.characteristics.dexterity }),
    INITIATIVE({ it.characteristics.initiative }),
    INTELLIGENCE({ it.characteristics.intelligence }),
    FELLOWSHIP({ it.characteristics.fellowship }),
    STRENGTH({ it.characteristics.strength }),
    TOUGHNESS({ it.characteristics.toughness }),
    WEAPON_SKILL({ it.characteristics.weaponSkill }),
    WILL_POWER({ it.characteristics.willPower });

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
