package cz.frantisekmasa.wfrp_master.inventory.domain.armour

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ArmourType : Parcelable {
    SOFT_LEATHER,
    BOILED_LEATHER,
    MAIL,
    PLATE,
    OTHER,
}