package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MeleeWeaponGroup : Parcelable {
    BASIC,
    BRAWLING,
    CAVALRY,
    FENCING,
    FLAIL,
    PARRY,
    POLEARM,
    TWO_HANDED,
}