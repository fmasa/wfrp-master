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
    val shortcut: StringResource,
) : NamedEnum {
    AGILITY(Str.characteristics_agility, Str.characteristics_agility_shortcut),
    BALLISTIC_SKILL(Str.characteristics_ballistic_skill, Str.characteristics_ballistic_skill_shortcut),
    DEXTERITY(Str.characteristics_dexterity, Str.characteristics_dexterity_shortcut),
    INITIATIVE(Str.characteristics_initiative, Str.characteristics_initiative_shortcut),
    INTELLIGENCE(Str.characteristics_intelligence, Str.characteristics_intelligence_shortcut),
    FELLOWSHIP(Str.characteristics_fellowship, Str.characteristics_fellowship_shortcut),
    STRENGTH(Str.characteristics_strength, Str.characteristics_strength_shortcut),
    TOUGHNESS(Str.characteristics_toughness, Str.characteristics_toughness_shortcut),
    WEAPON_SKILL(Str.characteristics_weapon_skill, Str.characteristics_weapon_skill_shortcut),
    WILL_POWER(Str.characteristics_will_power, Str.characteristics_will_power_shortcut);

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
