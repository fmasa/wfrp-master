package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.core.domain.Damage
import cz.frantisekmasa.wfrp_master.common.core.domain.Expression
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Parcelize
@Serializable
@Immutable
value class DamageExpression(val value: String) : Parcelable {
    init {
        require(
            expression(value, strengthBonus = 1).isDeterministic()
        ) { "Yards expression must be deterministic" }
    }

    enum class Constant(val value: String) {
        STRENGTH_BONUS("SB"),
        SPECIAL("Special"),
    }

    @Stable
    fun calculate(strengthBonus: Int, successLevels: Int): Damage {
        val damage = expression(value, strengthBonus).evaluate()

        return Damage((damage + successLevels).coerceAtLeast(0))
    }

    operator fun plus(other: DamageExpression) = DamageExpression("$value + ${other.value}")

    companion object {
        private fun expression(
            value: String,
            strengthBonus: Int,
        ): Expression {
            return Expression.fromString(
                when {
                    value.startsWith("+") -> value.substring(1)
                    value.startsWith("-") -> "0 $value"
                    else -> value
                },
                mapOf(
                    Constant.STRENGTH_BONUS.value to strengthBonus,
                    Constant.SPECIAL.value to 0,
                ),
            )
        }

        fun isValid(value: String) = runCatching { DamageExpression(value) }.isSuccess
    }
}
