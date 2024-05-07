package cz.frantisekmasa.wfrp_master.common.encounters.domain

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.Size
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min

@Serializable
@Immutable
@Parcelize
data class Wounds(
    val current: Int,
    val max: Int,
) : Parcelable {
    fun restore(restored: Int): Wounds = copy(current = min(max, current + restored))

    fun lose(lost: Int): Wounds = copy(current = max(0, current - lost))

    companion object {
        // See rulebook page 341
        fun calculateMax(
            size: Size,
            toughnessBonus: Int,
            strengthBonus: Int,
            willPowerBonus: Int,
        ): Int {
            return when (size) {
                Size.TINY -> 1
                Size.LITTLE -> toughnessBonus
                Size.SMALL -> (2 * toughnessBonus) + willPowerBonus
                Size.AVERAGE -> strengthBonus + (2 * toughnessBonus) + willPowerBonus
                Size.LARGE -> (strengthBonus + (2 * toughnessBonus) + willPowerBonus) * 2
                Size.ENORMOUS -> (strengthBonus + (2 * toughnessBonus) + willPowerBonus) * 4
                Size.MONSTROUS -> (strengthBonus + (2 * toughnessBonus) + willPowerBonus) * 8
            }
        }
    }

    init {
        require(current >= 0)
        require(max >= 0)
        require(current <= max)
    }
}
