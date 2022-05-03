package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize

@Parcelize
@Immutable
enum class WeaponEquip : Parcelable {
    PRIMARY_HAND,
    OFF_HAND,
    BOTH_HANDS,
}
