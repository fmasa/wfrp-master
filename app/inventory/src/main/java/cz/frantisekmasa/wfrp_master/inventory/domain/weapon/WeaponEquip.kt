package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class WeaponEquip : Parcelable {
    PRIMARY_HAND,
    OFF_HAND,
}
