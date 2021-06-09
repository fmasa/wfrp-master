package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class RangedWeaponGroup(val needsAmmo: Boolean = true) : Parcelable {
    BLACKPOWDER,
    BOW,
    CROSSBOW,
    ENTANGLING,
    ENGINEERING,
    EXPLOSIVES,
    SLING,
    THROWING(needsAmmo = false),
}