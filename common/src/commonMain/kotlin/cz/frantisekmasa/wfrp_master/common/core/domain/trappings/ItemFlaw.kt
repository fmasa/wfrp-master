package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@Immutable
enum class ItemFlaw: TrappingFeature, NamedEnum {
    BULKY,
    UGLY,
    SHODDY,
    UNRELIABLE;

    override val hasRating get() = false
    override val ratingUnit get() = null
    override val translatableName get() = when (this) {
        BULKY -> Str.trappings_flaws_bulky
        UGLY -> Str.trappings_flaws_ugly
        SHODDY -> Str.trappings_flaws_shoddy
        UNRELIABLE -> Str.trappings_flaws_unreliable
    }
}
