package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import androidx.annotation.StringRes
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum
import cz.muni.fi.rpg.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Reach(@StringRes override val nameRes: Int) : NamedEnum, Parcelable {
    PERSONAL(R.string.weapon_reach_personal),
    VERY_SHORT(R.string.weapon_reach_very_short),
    SHORT(R.string.weapon_reach_short),
    AVERAGE(R.string.weapon_reach_average),
    LONG(R.string.weapon_reach_long),
    VERY_LONG(R.string.weapon_reach_very_long),
    MASSIVE(R.string.weapon_reach_massive),
}
