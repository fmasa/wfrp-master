package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import androidx.annotation.StringRes
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.inventory.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class WeaponFlaw(
    @StringRes override val nameRes: Int,
    val hasRating: Boolean = false
) : NamedEnum, Parcelable {
    DANGEROUS(R.string.weapon_flaw_dangerous),
    IMPRECISE(R.string.weapon_flaw_imprecise),
    RELOAD(R.string.weapon_flaw_reload, hasRating = true),
    SLOW(R.string.weapon_flaw_slow),
    TIRING(R.string.weapon_flaw_tiring),
    UNDAMAGING(R.string.weapon_flaw_undamaging),
}