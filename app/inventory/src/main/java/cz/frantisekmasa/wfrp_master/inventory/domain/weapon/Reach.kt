package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Reach : Parcelable {
    PERSONAL,
    VERY_SHORT,
    SHORT,
    AVERAGE,
    LONG,
    VERY_LONG,
    MASSIVE,
}