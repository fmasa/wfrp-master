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
            return Advantage(UInt.MAX_VALUE)
        }

        return Advantage(
            Expression.fromString(value, constantsFrom(characteristics))
                .evaluate()
                .coerceAtLeast(0)
                .toUInt()
        )
    }

    companion object {
        const val MAX_LENGTH = 100

        fun isValid(value: String) = runCatching { AdvantageCapExpression(value) }.isSuccess
        fun constantsFrom(characteristics: Stats): Map<String, Int> {
            return Characteristic.values()
                .flatMap {
                    listOf(
                        it.getShortcutName() to characteristics.get(it),
                        it.getShortcutName() + "B" to characteristics.getBonus(it),
                    )
                }.toMap()
        }
    }
}