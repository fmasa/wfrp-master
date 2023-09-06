package cz.frantisekmasa.wfrp_master.common.core.domain.party.settings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.Expression
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Advantage
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
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
            return sequenceOf(
                "Ag" to Characteristic.AGILITY,
                "BS" to Characteristic.BALLISTIC_SKILL,
                "Dex" to Characteristic.DEXTERITY,
                "I" to Characteristic.INITIATIVE,
                "Int" to Characteristic.INTELLIGENCE,
                "Fel" to Characteristic.FELLOWSHIP,
                "S" to Characteristic.STRENGTH,
                "T" to Characteristic.TOUGHNESS,
                "WS" to Characteristic.WEAPON_SKILL,
                "WP" to Characteristic.WILL_POWER,
            ).flatMap { (shortcut, characteristic) ->
                sequenceOf(
                    shortcut to characteristics.get(characteristic),
                    shortcut + "B" to characteristics.getBonus(characteristic),
                )
            }.toMap()
        }
    }
}
