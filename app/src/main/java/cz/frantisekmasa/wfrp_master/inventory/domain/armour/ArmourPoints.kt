package cz.frantisekmasa.wfrp_master.inventory.domain.armour

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@JvmInline
@Parcelize
@Serializable
value class ArmourPoints(val value: Int) : Parcelable {
    init {
        require(value >= 0) { "Armour points cannot be negative" }
    }
}
