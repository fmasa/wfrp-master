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
    val extraToughnessBonusMultiplier: UInt = 0.toUInt(),
) : Parcelable {
    init {
        require(afterMultiplier >= 1.toUInt())
    }
}