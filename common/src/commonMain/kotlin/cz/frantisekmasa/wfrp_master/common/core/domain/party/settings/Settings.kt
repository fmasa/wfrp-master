package cz.frantisekmasa.wfrp_master.common.core.domain.party.settings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.Expression
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Advantage
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Parcelize
@Serializable
@Immutable
data class Settings(
    val initiativeStrategy: InitiativeStrategy = InitiativeStrategy.INITIATIVE_CHARACTERISTIC,
    val advantageSystem: AdvantageSystem = AdvantageSystem.CORE_RULEBOOK,
    val advantageCap: AdvantageCapExpression = AdvantageCapExpression(""),
) : Parcelable

@JvmInline
@Parcelize
@Serializable
@Immutable
value class AdvantageCapExpression(val value: String) : Parcelable {
    init {
        value.requireMaxLength(MAX_LENGTH, "value")
        require(
            value == "" ||
                Expression.fromString(value, constantsFrom(Stats.ZERO)).isDeterministic()
        ) { "Advantage expression must be deterministic" }
    }

    enum class Constant(
        val value: String,
        override val translatableName: StringResource,
        val provider: (Stats) -> Int,
    ) : NamedEnum {
        AGILITY(
            "Ag",
            Str.characteristics_agility_shortcut, { it.get(Characteristic.AGILITY) },
        ),
        AGILITY_BONUS(
            "AgB",
            Str.characteristics_agility_bonus_shortcut,
            { it.getBonus(Characteristic.AGILITY) }
        ),
        BALLISTIC_SKILL(
            "BS",
            Str.characteristics_ballistic_skill_shortcut,
            { it.get(Characteristic.BALLISTIC_SKILL) }
        ),
        BALLISTIC_SKILL_BONUS(
            "BSB",
            Str.characteristics_ballistic_skill_bonus_shortcut,
            { it.getBonus(Characteristic.BALLISTIC_SKILL) }
        ),
        DEXTERITY(
            "Dex",
            Str.characteristics_dexterity_shortcut,
            { it.get(Characteristic.DEXTERITY) }
        ),
        DEXTERITY_BONUS(
            "DexB",
            Str.characteristics_dexterity_bonus_shortcut,
            { it.getBonus(Characteristic.DEXTERITY) }
        ),
        INITIATIVE(
            "I",
            Str.characteristics_initiative_shortcut,
            { it.get(Characteristic.INITIATIVE) }
        ),
        INITIATIVE_BONUS(
            "IB",
            Str.characteristics_initiative_bonus_shortcut,
            { it.getBonus(Characteristic.INITIATIVE) }
        ),
        INTELLIGENCE(
            "Int",
            Str.characteristics_intelligence_shortcut,
            { it.get(Characteristic.INTELLIGENCE) }
        ),
        INTELLIGENCE_BONUS(
            "IntB",
            Str.characteristics_intelligence_bonus_shortcut,
            { it.getBonus(Characteristic.INTELLIGENCE) }
        ),
        FELLOWSHIP(
            "Fel",
            Str.characteristics_fellowship_shortcut,
            { it.get(Characteristic.FELLOWSHIP) }
        ),
        FELLOWSHIP_BONUS(
            "FelB",
            Str.characteristics_fellowship_bonus_shortcut,
            { it.getBonus(Characteristic.FELLOWSHIP) }
        ),
        STRENGTH(
            "S",
            Str.characteristics_strength_shortcut,
            { it.get(Characteristic.STRENGTH) },
        ),
        STRENGTH_BONUS(
            "SB",
            Str.characteristics_strength_bonus_shortcut,
            { it.getBonus(Characteristic.STRENGTH) }
        ),
        TOUGHNESS(
            "T",
            Str.characteristics_toughness_shortcut,
            { it.get(Characteristic.TOUGHNESS) }
        ),
        TOUGHNESS_BONUS(
            "TB",
            Str.characteristics_toughness_bonus_shortcut,
            { it.getBonus(Characteristic.TOUGHNESS) }
        ),
        WEAPON_SKILL(
            "WS",
            Str.characteristics_weapon_skill_shortcut,
            { it.get(Characteristic.WEAPON_SKILL) }
        ),
        WEAPON_SKILL_BONUS(
            "WSB",
            Str.characteristics_weapon_skill_bonus_shortcut,
            { it.getBonus(Characteristic.WEAPON_SKILL) }
        ),
        WILL_POWER(
            "WP",
            Str.characteristics_will_power_shortcut,
            { it.get(Characteristic.WILL_POWER) }
        ),
        WILL_POWER_BONUS(
            "WPB",
            Str.characteristics_will_power_bonus_shortcut,
            { it.getBonus(Characteristic.WILL_POWER) }
        ),
    }

    fun calculate(characteristics: Stats): Advantage {
        if (value == "") {
            return Advantage(Int.MAX_VALUE)
        }

        return Advantage(
            Expression.fromString(value, constantsFrom(characteristics))
                .evaluate()
                .coerceAtLeast(0)
        )
    }

    companion object {
        const val MAX_LENGTH = 100

        fun isValid(value: String) = runCatching { AdvantageCapExpression(value) }.isSuccess
        fun constantsFrom(characteristics: Stats): Map<String, Int> {
            return Constant.values().associate { it.value to it.provider(characteristics) }
        }
    }
}
