package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@Immutable
enum class WeaponQuality(
    override val hasRating: Boolean = false,
    override val ratingUnit: String? = null,
) : Quality {
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
    SLASH(hasRating = true, ratingUnit = "A"), // Up in Arms
    SPREAD(hasRating = true), // Up in Arms
    TRIP, // Up in Arms
    TRAP_BLADE,
    UNBREAKABLE,
    WRAP, ;

    override val translatableName get() =
        when (this) {
            ACCURATE -> Str.weapons_qualities_accurate
            BLACKPOWDER -> Str.weapons_qualities_blackpowder
            BLAST -> Str.weapons_qualities_blast
            DAMAGING -> Str.weapons_qualities_damaging
            DEFENSIVE -> Str.weapons_qualities_defensive
            DISTRACT -> Str.weapons_qualities_distract
            ENTANGLE -> Str.weapons_qualities_entangle
            FAST -> Str.weapons_qualities_fast
            HACK -> Str.weapons_qualities_hack
            IMPACT -> Str.weapons_qualities_impact
            IMPALE -> Str.weapons_qualities_impale
            PENETRATING -> Str.weapons_qualities_penetrating
            PISTOL -> Str.weapons_qualities_pistol
            PRECISE -> Str.weapons_qualities_precise
            PUMMEL -> Str.weapons_qualities_pummel
            REPEATER -> Str.weapons_qualities_repeater
            SHIELD -> Str.weapons_qualities_shield
            SLASH -> Str.weapons_qualities_slash
            SPREAD -> Str.weapons_qualities_spread
            TRIP -> Str.weapons_qualities_trip
            TRAP_BLADE -> Str.weapons_qualities_trap_blade
            UNBREAKABLE -> Str.weapons_qualities_unbreakable
            WRAP -> Str.weapons_qualities_wrap
        }
}
