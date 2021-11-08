package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import androidx.annotation.StringRes
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum
import cz.muni.fi.rpg.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MeleeWeaponGroup(@StringRes override val nameRes: Int) : NamedEnum, Parcelable {
    BASIC(R.string.melee_weapon_group_basic),
    BRAWLING(R.string.melee_weapon_group_brawling),
    CAVALRY(R.string.melee_weapon_group_cavalry),
    FENCING(R.string.melee_weapon_group_fencing),
    FLAIL(R.string.melee_weapon_group_flail),
    PARRY(R.string.melee_weapon_group_parry),
    POLEARM(R.string.melee_weapon_group_polearm),
    TWO_HANDED(R.string.melee_weapon_group_two_handed),
}
