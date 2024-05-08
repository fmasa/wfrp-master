package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Damage
import cz.frantisekmasa.wfrp_master.common.core.domain.Expression
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Parcelize
@Serializable
@Immutable
value class DamageExpression(val value: String) : Parcelable {
    init {
        require(
            expression(value, strengthBonus = 1).isDeterministic(),
        ) { "Yards expression must be deterministic" }
    }

    enum class Constant(
        override val value: String,
        override val translatableName: StringResource,
    ) : Expression.Constant {
        STRENGTH_BONUS("SB", Str.characteristics_strength_bonus_shortcut),
        SPECIAL("Special", Str.trappings_expression_constants_damage_special),
    }

    @Composable
    @Stable
    fun formatted(): String {
        return Expression.formatter<Constant>()(value)
    }

    @Stable
    fun calculate(
        strengthBonus: Int,
        successLevels: Int,
    ): Damage {
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
