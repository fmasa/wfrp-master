package cz.frantisekmasa.wfrp_master.common.core.domain.character

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Immutable
data class WoundsModifiers(
    val afterMultiplier: UInt = 1.toUInt(),
    val extraToughnessBonusMultiplier: Int = 0,
) : Parcelable {
    init {
        require(extraToughnessBonusMultiplier >= 0)
        require(afterMultiplier >= 1.toUInt())
    }
}