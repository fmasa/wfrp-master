package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonValue
import cz.frantisekmasa.wfrp_master.core.domain.Expression
import kotlinx.parcelize.Parcelize

@Parcelize
data class AmmunitionRangeExpression(
    @JsonValue
    private val value: String
) : Parcelable {
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
        fun parse(value: String): Result<AmmunitionRangeExpression> {
            return runCatching { AmmunitionRangeExpression(value) }
        }

        fun isValid(value: String) = parse(value).isSuccess
    }
}
