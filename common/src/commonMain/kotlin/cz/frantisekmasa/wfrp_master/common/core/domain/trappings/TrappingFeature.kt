package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable

interface TrappingFeature : NamedEnum, Parcelable {
    val hasRating: Boolean
    val ratingUnit: String? get() = null
}
