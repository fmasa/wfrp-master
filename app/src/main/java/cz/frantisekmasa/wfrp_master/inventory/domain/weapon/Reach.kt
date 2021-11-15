package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Reach(override val nameResolver: (strings: Strings) -> String) : NamedEnum, Parcelable {
    PERSONAL({ it.weapons.reach.personal }),
    VERY_SHORT({ it.weapons.reach.veryShort }),
    SHORT({ it.weapons.reach.short }),
    AVERAGE({ it.weapons.reach.average }),
    LONG({ it.weapons.reach.long }),
    VERY_LONG({ it.weapons.reach.veryLong }),
    MASSIVE({ it.weapons.reach.massive }),
}
