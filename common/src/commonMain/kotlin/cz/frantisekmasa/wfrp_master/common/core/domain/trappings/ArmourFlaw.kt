package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@Immutable
enum class ArmourFlaw() : Flaw {
    PARTIAL,
    WEAKPOINTS,
    ;

    override val translatableName get() =
        when (this) {
            PARTIAL -> Str.armour_flaws_partial
            WEAKPOINTS -> Str.armour_flaws_weakpoints
        }

    override val hasRating: Boolean get() = false
}
