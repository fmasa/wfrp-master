package cz.frantisekmasa.wfrp_master.inventory.domain.armour

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
enum class ArmourType(override val nameResolver: (strings: Strings) -> String) : NamedEnum, Parcelable {
    SOFT_LEATHER({ it.armour.types.softLeather }),
    BOILED_LEATHER({ it.armour.types.boiledLeather }),
    MAIL({ it.armour.types.mail }),
    PLATE({ it.armour.types.plate }),
    OTHER({ it.armour.types.other }),
}
