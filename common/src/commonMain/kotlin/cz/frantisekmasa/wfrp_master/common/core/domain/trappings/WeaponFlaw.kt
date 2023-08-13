package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class WeaponFlaw(
    override val translatableName: StringResource,
    override val hasRating: Boolean = false
) : Flaw {
    DANGEROUS(Str.weapons_flaws_dangerous),
    IMPRECISE(Str.weapons_flaws_imprecise),
    RELOAD(Str.weapons_flaws_reload, hasRating = true),
    SLOW(Str.weapons_flaws_slow),
    TIRING(Str.weapons_flaws_tiring),
    UNBALANCED(Str.weapons_flaws_unbalanced), // Up in Arms
    UNDAMAGING(Str.weapons_flaws_undamaging),
}
