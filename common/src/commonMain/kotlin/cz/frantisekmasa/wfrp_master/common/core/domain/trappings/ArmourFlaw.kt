package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@Immutable
enum class ArmourFlaw() : Flaw {
    PARTIAL,
    REQUIRES_KIT,
    WEAKPOINTS,
    ;

    override val translatableName get() =
        when (this) {
            PARTIAL -> Str.armour_flaws_partial
            REQUIRES_KIT -> Str.armour_flaws_requires_kit
            WEAKPOINTS -> Str.armour_flaws_weakpoints
        }

    override val hasRating: Boolean get() = false
}
