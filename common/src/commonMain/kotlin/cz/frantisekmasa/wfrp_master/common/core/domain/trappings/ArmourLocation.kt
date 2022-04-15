package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
@Immutable
enum class ArmourLocation(
    override val nameResolver: (strings: Strings) -> String,
) : NamedEnum, Parcelable {
    HEAD({ it.armour.locations.head }),
    BODY({ it.armour.locations.body }),
    LEFT_ARM({ it.armour.locations.leftArm }),
    RIGHT_ARM({ it.armour.locations.rightArm }),
    LEFT_LEG({ it.armour.locations.leftLeg }),
    RIGHT_LEG({ it.armour.locations.rightLeg }),
}
