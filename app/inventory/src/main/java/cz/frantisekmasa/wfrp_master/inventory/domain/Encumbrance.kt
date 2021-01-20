package cz.frantisekmasa.wfrp_master.inventory.domain

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonValue
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import kotlinx.parcelize.Parcelize

@Parcelize
data class Encumbrance(
    @JsonValue
    private val value: Double,
) : Parcelable {
    companion object {
        val Zero: Encumbrance = Encumbrance(0.0)

        fun maximumForCharacter(characteristics: Stats): Encumbrance = Encumbrance(
            (characteristics.strengthBonus + characteristics.toughnessBonus).toDouble()
        )
    }

    init {
        require(value >= 0) { "Encumbrance must be greater than or equal to 0" }
    }

    operator fun compareTo(other: Encumbrance) = value.compareTo(other.value)
    operator fun times(multiplier: Int) = Encumbrance(value * multiplier)
    operator fun plus(addend: Encumbrance) = Encumbrance(value + addend.value)

    override fun toString(): String = value.formatWithMaximumDecimalPlaces(3)
}

fun Iterable<Encumbrance>.sum(): Encumbrance = fold(Encumbrance.Zero) { a, b -> a + b }

private fun Double.formatWithMaximumDecimalPlaces(decimals: Int) =
    "%.${decimals}f".format(this)
        .trimEnd('0')
        .trimEnd('.')
