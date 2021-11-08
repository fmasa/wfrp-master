package cz.frantisekmasa.wfrp_master.inventory.domain.armour

import android.os.Parcelable
import androidx.annotation.StringRes
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum
import cz.muni.fi.rpg.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ArmourLocation(@StringRes override val nameRes: Int) : NamedEnum, Parcelable {
    HEAD(R.string.armor_head),
    BODY(R.string.armor_body),
    LEFT_ARM(R.string.armor_left_arm),
    RIGHT_ARM(R.string.armor_right_arm),
    LEFT_LEG(R.string.armor_left_leg),
    RIGHT_LEG(R.string.armor_right_leg),
}
