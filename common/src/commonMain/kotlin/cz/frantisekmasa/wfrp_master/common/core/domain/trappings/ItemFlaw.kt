package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource

enum class ItemFlaw(
    override val translatableName: StringResource,
) : TrappingFeature, NamedEnum {
    BULKY(Str.trappings_flaws_bulky),
    UGLY(Str.trappings_flaws_ugly),
    SHODDY(Str.trappings_flaws_shoddy),
    UNRELIABLE(Str.trappings_flaws_unreliable);

    override val hasRating get() = false
    override val ratingUnit get() = null
}
