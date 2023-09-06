package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class WeaponQuality(
    override val translatableName: StringResource,
    override val hasRating: Boolean = false,
    override val ratingUnit: String? = null,
) : Quality {
    ACCURATE(Str.weapons_qualities_accurate),
    BLACKPOWDER(Str.weapons_qualities_blackpowder),
    BLAST(Str.weapons_qualities_blast, hasRating = true),
    DAMAGING(Str.weapons_qualities_damaging),
    DEFENSIVE(Str.weapons_qualities_defensive),
    DISTRACT(Str.weapons_qualities_distract),
    ENTANGLE(Str.weapons_qualities_entangle),
    FAST(Str.weapons_qualities_fast),
    HACK(Str.weapons_qualities_hack),
    IMPACT(Str.weapons_qualities_impact),
    IMPALE(Str.weapons_qualities_impale),
    PENETRATING(Str.weapons_qualities_penetrating),
    PISTOL(Str.weapons_qualities_pistol),
    PRECISE(Str.weapons_qualities_precise),
    PUMMEL(Str.weapons_qualities_pummel),
    REPEATER(Str.weapons_qualities_repeater, hasRating = true),
    SHIELD(Str.weapons_qualities_shield, hasRating = true),
    SLASH(Str.weapons_qualities_slash, hasRating = true, ratingUnit = "A"), // Up in Arms
    SPREAD(Str.weapons_qualities_spread, hasRating = true), // Up in Arms
    TRIP(Str.weapons_qualities_trip), // Up in Arms
    TRAP_BLADE(Str.weapons_qualities_trap_blade),
    UNBREAKABLE(Str.weapons_qualities_unbreakable),
    WRAP(Str.weapons_qualities_wrap),
}
