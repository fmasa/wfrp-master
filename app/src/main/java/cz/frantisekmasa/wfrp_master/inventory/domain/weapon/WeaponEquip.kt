package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
enum class WeaponEquip : Parcelable {
    PRIMARY_HAND,
    OFF_HAND,
}
