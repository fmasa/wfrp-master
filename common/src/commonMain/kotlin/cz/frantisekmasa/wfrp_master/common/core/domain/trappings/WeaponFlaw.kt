package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class WeaponFlaw(
    override val hasRating: Boolean = false
) : Flaw {
    DANGEROUS,
    IMPRECISE,
    RELOAD(hasRating = true),
    SLOW,
    TIRING,
    UNBALANCED, // Up in Arms
    UNDAMAGING;

    override val translatableName: StringResource get() = when (this) {
        DANGEROUS -> Str.weapons_flaws_dangerous
        IMPRECISE -> Str.weapons_flaws_imprecise
        RELOAD -> Str.weapons_flaws_reload
        SLOW -> Str.weapons_flaws_slow
        TIRING -> Str.weapons_flaws_tiring
        UNBALANCED -> Str.weapons_flaws_unbalanced
        UNDAMAGING -> Str.weapons_flaws_undamaging
    }
}
