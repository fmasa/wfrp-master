package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import dev.icerock.moko.resources.StringResource

@Immutable
enum class Characteristic(
    override val translatableName: StringResource,
) : NamedEnum {
    AGILITY(Str.characteristics_agility),
    BALLISTIC_SKILL(Str.characteristics_ballistic_skill),
    DEXTERITY(Str.characteristics_dexterity),
    INITIATIVE(Str.characteristics_initiative),
    INTELLIGENCE(Str.characteristics_intelligence),
    FELLOWSHIP(Str.characteristics_fellowship),
    STRENGTH(Str.characteristics_strength),
    TOUGHNESS(Str.characteristics_toughness),
    WEAPON_SKILL(Str.characteristics_weapon_skill),
    WILL_POWER(Str.characteristics_will_power);

    @Stable
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
    fun getIcon(): Resources.Drawable = when (this) {
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

    companion object {
        val ORDER = listOf(
            WEAPON_SKILL,
            BALLISTIC_SKILL,
            STRENGTH,
            TOUGHNESS,
            INITIATIVE,
            AGILITY,
            DEXTERITY,
            INTELLIGENCE,
            WILL_POWER,
            FELLOWSHIP,
        )
    }
}
