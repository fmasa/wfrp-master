package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class WeaponFlaw(val hasRating: Boolean = false) : Parcelable {
    DANGEROUS,
    IMPRECISE,
    RELOAD(hasRating = true),
    SLOW,
    TIRING,
    UNDAMAGING,
}