package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Expression
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Parcelize
@Serializable
@Immutable
value class AmmunitionRangeExpression(val value: String) : Parcelable {
    init {
        require(
            Expression.fromString(
                value,
                Constant.values().associate { it.value to 1 }
            ).isDeterministic()
        ) { "Yards expression must be deterministic" }
    }

    enum class Constant(
        override val value: String,
        override val translatableName: StringResource,
    ) : Expression.Constant {
        WEAPON_RANGE("WeaponRange", Str.trappings_expression_constants_weapon_range)
    }

    @Composable
    @Stable
    fun formatted(): String {
        return Expression.formatter<Constant>()(value)
    }

    companion object {
        fun isValid(value: String) = runCatching { AmmunitionRangeExpression(value) }.isSuccess
    }
}
