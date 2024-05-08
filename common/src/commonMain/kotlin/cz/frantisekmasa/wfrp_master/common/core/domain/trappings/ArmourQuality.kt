package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@Immutable
enum class ArmourQuality : Quality {
    FLEXIBLE,
    IMPENETRABLE,
    ;

    override val translatableName get() =
        when (this) {
            FLEXIBLE -> Str.armour_qualities_flexible
            IMPENETRABLE -> Str.armour_qualities_impenetrable
        }
    override val hasRating: Boolean get() = false
}
