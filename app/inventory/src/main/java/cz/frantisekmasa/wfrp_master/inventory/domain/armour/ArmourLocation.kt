package cz.frantisekmasa.wfrp_master.inventory.domain.armour

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ArmourLocation : Parcelable {
    HEAD,
    BODY,
    LEFT_ARM,
    RIGHT_ARM,
    LEFT_LEG,
    RIGHT_LEG,
}