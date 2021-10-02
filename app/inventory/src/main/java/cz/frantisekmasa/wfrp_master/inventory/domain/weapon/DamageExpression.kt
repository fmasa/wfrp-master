package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.core.domain.Expression
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@JvmInline
@Parcelize
@Serializable
value class DamageExpression(val value: String) : Parcelable {
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
        STRENGTH_BONUS("SB")
    }

    companion object {
        fun isValid(value: String) = runCatching { DamageExpression(value) }.isSuccess
    }
}
