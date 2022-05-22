package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable

interface TrappingFeature: NamedEnum, Parcelable {
    val hasRating: Boolean
}