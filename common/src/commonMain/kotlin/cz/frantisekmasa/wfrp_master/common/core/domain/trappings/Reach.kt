package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
@Immutable
enum class Reach(override val nameResolver: (strings: Strings) -> String) : NamedEnum, Parcelable {
    PERSONAL({ it.weapons.reach.personal }),
    VERY_SHORT({ it.weapons.reach.veryShort }),
    SHORT({ it.weapons.reach.short }),
    AVERAGE({ it.weapons.reach.average }),
    LONG({ it.weapons.reach.long }),
    VERY_LONG({ it.weapons.reach.veryLong }),
    MASSIVE({ it.weapons.reach.massive }),
}
