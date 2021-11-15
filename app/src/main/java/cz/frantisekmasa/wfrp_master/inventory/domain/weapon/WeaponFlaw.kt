package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings
import kotlinx.parcelize.Parcelize

@Parcelize
enum class WeaponFlaw(
    override val nameResolver: (strings: Strings) -> String,
    val hasRating: Boolean = false
) : NamedEnum, Parcelable {
    DANGEROUS({ it.weapons.flaws.dangerous }),
    IMPRECISE({ it.weapons.flaws.imprecise }),
    RELOAD({ it.weapons.flaws.reload }, hasRating = true),
    SLOW({ it.weapons.flaws.slow }),
    TIRING({ it.weapons.flaws.tiring }),
    UNDAMAGING({ it.weapons.flaws.undamaging }),
}
