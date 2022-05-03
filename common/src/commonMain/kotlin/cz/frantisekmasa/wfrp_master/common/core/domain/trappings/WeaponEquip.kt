package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
@Immutable
enum class WeaponEquip(override val nameResolver: (strings: Strings) -> String) : NamedEnum, Parcelable {
    PRIMARY_HAND({ it.weapons.equip.primaryHand }),
    OFF_HAND({ it.weapons.equip.offHand }),
    BOTH_HANDS({ it.weapons.equip.bothHands }),
}
