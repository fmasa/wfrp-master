package cz.frantisekmasa.wfrp_master.inventory.domain.armour

import android.os.Parcelable
import androidx.annotation.StringRes
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum
import cz.muni.fi.rpg.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ArmourType(@StringRes override val nameRes: Int) : NamedEnum, Parcelable {
    SOFT_LEATHER(R.string.armor_type_soft_leather),
    BOILED_LEATHER(R.string.armor_type_boiled_leather),
    MAIL(R.string.armor_type_mail),
    PLATE(R.string.armor_type_plate),
    OTHER(R.string.armor_type_other),
}
