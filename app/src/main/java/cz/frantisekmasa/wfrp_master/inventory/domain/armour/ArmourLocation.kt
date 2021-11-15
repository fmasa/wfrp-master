package cz.frantisekmasa.wfrp_master.inventory.domain.armour

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ArmourLocation(override val nameResolver: (strings: Strings) -> String) : NamedEnum, Parcelable {
    HEAD({ it.armourHead }),
    BODY({ it.armourBody }),
    LEFT_ARM({ it.armourLeftArm }),
    RIGHT_ARM({ it.armourRightArm }),
    LEFT_LEG({ it.armourLeftLeg }),
    RIGHT_LEG({ it.armourRightLeg }),
}
