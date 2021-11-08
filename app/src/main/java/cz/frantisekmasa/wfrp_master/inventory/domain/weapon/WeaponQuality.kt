package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import androidx.annotation.StringRes
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum
import cz.muni.fi.rpg.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class WeaponQuality(
    @StringRes override val nameRes: Int,
    val hasRating: Boolean = false,
) : NamedEnum, Parcelable {
    ACCURATE(R.string.weapon_quality_accurate),
    BLACKPOWDER(R.string.weapon_quality_blackpowder),
    BLAST(R.string.weapon_quality_blast, hasRating = true),
    DAMAGING(R.string.weapon_quality_damaging),
    DEFENSIVE(R.string.weapon_quality_defensive),
    DISTRACT(R.string.weapon_quality_distract),
    ENTANGLE(R.string.weapon_quality_entangle),
    FAST(R.string.weapon_quality_fast),
    HACK(R.string.weapon_quality_hack),
    IMPACT(R.string.weapon_quality_impact),
    IMPALE(R.string.weapon_quality_impale),
    PENETRATING(R.string.weapon_quality_penetrating),
    PISTOL(R.string.weapon_quality_pistol),
    PRECISE(R.string.weapon_quality_precise),
    PUMMEL(R.string.weapon_quality_pummel),
    REPEATER(R.string.weapon_quality_repeater, hasRating = true),
    SHIELD(R.string.weapon_quality_shield, hasRating = true),
    TRAP_BLADE(R.string.weapon_quality_trap_blade),
    UNBREAKABLE(R.string.weapon_quality_unbreakable),
    WRAP(R.string.weapon_quality_wrap),
}
