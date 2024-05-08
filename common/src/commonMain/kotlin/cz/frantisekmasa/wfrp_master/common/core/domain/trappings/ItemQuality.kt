package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class ItemQuality : TrappingFeature, NamedEnum {
    DURABLE,
    FINE,
    LIGHTWEIGHT,
    PRACTICAL,
    ;

    override val hasRating get() = false
    override val ratingUnit get() = null
    override val translatableName: StringResource get() =
        when (this) {
            DURABLE -> Str.trappings_qualities_durable
            FINE -> Str.trappings_qualities_fine
            LIGHTWEIGHT -> Str.trappings_qualities_lightweight
            PRACTICAL -> Str.trappings_qualities_practical
        }
}
