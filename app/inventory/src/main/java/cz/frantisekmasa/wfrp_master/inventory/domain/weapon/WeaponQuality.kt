package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class WeaponQuality(val hasRating: Boolean = false) : Parcelable {
    ACCURATE,
    BLACKPOWDER,
    BLAST(hasRating = true),
    DAMAGING,
    DEFENSIVE,
    DISTRACT,
    ENTANGLE,
    FAST,
    HACK,
    IMPACT,
    IMPALE,
    PENETRATING,
    PISTOL,
    PRECISE,
    PUMMEL,
    REPEATER(hasRating = true),
    SHIELD(hasRating = true),
    TRAP_BLADE,
    UNBREAKABLE,
    WRAP,
}
