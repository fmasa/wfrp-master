package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import androidx.annotation.StringRes
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum
import cz.muni.fi.rpg.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class RangedWeaponGroup(
    @StringRes override val nameRes: Int,
    val needsAmmo: Boolean = true
) : NamedEnum, Parcelable {
    BLACKPOWDER(R.string.ranged_weapon_group_blackpowder),
    BOW(R.string.ranged_weapon_group_bow),
    CROSSBOW(R.string.ranged_weapon_group_crossbow),
    ENTANGLING(R.string.ranged_weapon_group_entangling),
    ENGINEERING(R.string.ranged_weapon_group_engineering),
    EXPLOSIVES(R.string.ranged_weapon_group_explosives),
    SLING(R.string.ranged_weapon_group_sling),
    THROWING(R.string.ranged_weapon_group_throwing, needsAmmo = false),
}
