package cz.frantisekmasa.wfrp_master.inventory.domain.armour

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ArmourType(override val nameResolver: (strings: Strings) -> String) : NamedEnum, Parcelable {
    SOFT_LEATHER({ it.armourTypeSoftLeather }),
    BOILED_LEATHER({ it.armourTypeBoiledLeather }),
    MAIL({ it.armourTypeMail }),
    PLATE({ it.armourTypePlate }),
    OTHER({ it.armourTypeOther }),
}
