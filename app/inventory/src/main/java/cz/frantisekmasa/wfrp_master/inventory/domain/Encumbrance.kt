package cz.frantisekmasa.wfrp_master.inventory.domain

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonValue
import cz.frantisekmasa.wfrp_master.core.domain.Stats
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Parcelize
data class Encumbrance(
    @JsonValue
    private val value: Long,
) : Parcelable {
    companion object {
        val Zero: Encumbrance = Encumbrance(0)

        private const val SUBUNITS = 1000L
        private val MAX_DECIMAL_PLACES = kotlin.math.log10(SUBUNITS.toDouble()).roundToInt()

        fun maximumForCharacter(characteristics: Stats): Encumbrance =
            fromInt(characteristics.strengthBonus + characteristics.toughnessBonus)

        fun fromString(value: String): Encumbrance =
            Encumbrance((value.toFloat() * SUBUNITS).roundToLong())

        private fun fromInt(value: Int) = Encumbrance(value * SUBUNITS)
    }

    init {
        require(value >= 0) { "Encumbrance must be greater than or equal to 0" }
    }


    operator fun compareTo(other: Encumbrance) = value.compareTo(other.value)
    operator fun times(multiplier: Int) = Encumbrance(value * multiplier)
    operator fun plus(addend: Encumbrance) = Encumbrance(value + addend.value)

    override fun toString(): String {
        return (value / SUBUNITS).toString() +
                ("." + (value % SUBUNITS).toString().padStart(MAX_DECIMAL_PLACES, '0'))
                    .trimEnd('0', '.')
    }
}

fun String.toEncumbranceOrNull(): Encumbrance? =
    try {
        Encumbrance.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }

fun Iterable<Encumbrance>.sum(): Encumbrance = fold(Encumbrance.Zero) { a, b -> a + b }