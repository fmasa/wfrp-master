package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
@Immutable
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
