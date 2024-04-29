package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@Immutable
enum class ArmourType: NamedEnum, Parcelable {
    SOFT_LEATHER,
    BOILED_LEATHER,
    MAIL,
    PLATE,
    OTHER,;

    override val translatableName get() = when (this) {
        SOFT_LEATHER -> Str.armour_types_soft_leather
        BOILED_LEATHER -> Str.armour_types_boiled_leather
        MAIL -> Str.armour_types_mail
        PLATE -> Str.armour_types_plate
        OTHER -> Str.armour_types_other
    }
}
