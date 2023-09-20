package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Expression
import cz.frantisekmasa.wfrp_master.common.core.domain.Yards
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Parcelize
@Serializable
@Immutable
value class WeaponRangeExpression(val value: String) : Parcelable {
    init {
        require(expression(value, strengthBonus = 1).isDeterministic()) {
            "Yards expression must be deterministic"
        }
    }

    enum class Constant(
        override val value: String,
        override val translatableName: StringResource,
    ) : Expression.Constant {
        STRENGTH_BONUS("SB", Str.characteristics_strength_bonus_shortcut)
    }

    @Composable
    @Stable
    fun formatted(): String {
        return Expression.formatter<Constant>()(value)
    }

    @Stable
    fun calculate(strengthBonus: Int): Yards {
        return Yards(expression(value, strengthBonus).evaluate().coerceAtLeast(0))
    }

    companion object {
        fun parse(value: String): Result<WeaponRangeExpression> {
            return kotlin.runCatching { WeaponRangeExpression(value) }
        }

        private fun expression(
            value: String,
            strengthBonus: Int,
        ): Expression {
            return Expression.fromString(
                value,
                mapOf(Constant.STRENGTH_BONUS.value to strengthBonus)
            )
        }

        fun isValid(value: String) = parse(value).isSuccess
    }
}
