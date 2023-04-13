package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
@Immutable
enum class WeaponQuality(
    override val nameResolver: (strings: Strings) -> String,
    override val hasRating: Boolean = false,
    override val ratingUnit: String? = null,
) : Quality {
    ACCURATE({ it.weapons.qualities.accurate }),
    BLACKPOWDER({ it.weapons.qualities.blackpowder }),
    BLAST({ it.weapons.qualities.blast }, hasRating = true),
    DAMAGING({ it.weapons.qualities.damaging }),
    DEFENSIVE({ it.weapons.qualities.defensive }),
    DISTRACT({ it.weapons.qualities.distract }),
    ENTANGLE({ it.weapons.qualities.entangle }),
    FAST({ it.weapons.qualities.fast }),
    HACK({ it.weapons.qualities.hack }),
    IMPACT({ it.weapons.qualities.impact }),
    IMPALE({ it.weapons.qualities.impale }),
    PENETRATING({ it.weapons.qualities.penetrating }),
    PISTOL({ it.weapons.qualities.pistol }),
    PRECISE({ it.weapons.qualities.precise }),
    PUMMEL({ it.weapons.qualities.pummel }),
    REPEATER({ it.weapons.qualities.repeater }, hasRating = true),
    SHIELD({ it.weapons.qualities.shield }, hasRating = true),
    SLASH({ it.weapons.qualities.slash }, hasRating = true, ratingUnit = "A"), // Up in Arms
    SPREAD({ it.weapons.qualities.spread }, hasRating = true), // Up in Arms
    TRIP({ it.weapons.qualities.trip }), // Up in Arms
    TRAP_BLADE({ it.weapons.qualities.trapBlade }),
    UNBREAKABLE({ it.weapons.qualities.unbreakable }),
    WRAP({ it.weapons.qualities.wrap }),
}
