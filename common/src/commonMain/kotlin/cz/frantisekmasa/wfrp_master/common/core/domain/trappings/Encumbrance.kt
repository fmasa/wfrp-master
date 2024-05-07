package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.text.DecimalFormat
import kotlin.jvm.JvmInline

@Parcelize
@JvmInline
@Serializable
@Immutable
value class Encumbrance(private val value: Double) : Parcelable {
    companion object {
        val One: Encumbrance = Encumbrance(1.0)
        val Zero: Encumbrance = Encumbrance(0.0)
        private val formatter = DecimalFormat("#,##0.###")

        fun maximumForCharacter(characteristics: Stats): Encumbrance =
            Encumbrance(
                (characteristics.strengthBonus + characteristics.toughnessBonus).toDouble(),
            )
    }

    init {
        require(value >= 0) { "Encumbrance must be greater than or equal to 0" }
    }

    operator fun compareTo(other: Encumbrance) = value.compareTo(other.value)

    operator fun times(multiplier: Int) = Encumbrance(value * multiplier)

    operator fun plus(addend: Encumbrance) = Encumbrance(value + addend.value)

    operator fun minus(other: Encumbrance) = Encumbrance((value - other.value).coerceAtLeast(0.0))

    override fun toString(): String = formatter.format(value)
}

fun Iterable<Encumbrance>.sum(): Encumbrance = fold(Encumbrance.Zero) { a, b -> a + b }
