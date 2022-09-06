package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.Expression
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
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
                Constant.values()
                    .map { it.value to 1 }
                    .toMap()
            ).isDeterministic()
        ) { "Yards expression must be deterministic" }
    }

    enum class Constant(val value: String) {
        WEAPON_RANGE("WeaponRange")
    }

    companion object {
        fun isValid(value: String) = runCatching { AmmunitionRangeExpression(value) }.isSuccess
    }
}
