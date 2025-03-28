package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@Immutable
enum class ArmourQuality : Quality {
    FLEXIBLE,
    IMPENETRABLE,
    OVERCOAT,
    REINFORCED,
    VISOR,
    ;

    override val translatableName get() =
        when (this) {
            FLEXIBLE -> Str.armour_qualities_flexible
            IMPENETRABLE -> Str.armour_qualities_impenetrable
            OVERCOAT -> Str.armour_qualities_overcoat
            REINFORCED -> Str.armour_qualities_reinforced
            VISOR -> Str.armour_qualities_visor
        }
    override val hasRating: Boolean get() = false
}
