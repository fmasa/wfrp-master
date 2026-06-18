package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class ArmourType(override val translatableName: StringResource) : NamedEnum, Parcelable {
    BOILED_LEATHER(Str.armour_types_boiled_leather),
    BRIGANDINE(Str.armour_types_brigandine),
    SOFT_KIT(Str.armour_types_soft_kit),
    SOFT_LEATHER(Str.armour_types_soft_leather),
    MAIL(Str.armour_types_mail),
    PLATE(Str.armour_types_plate),
    OTHER(Str.armour_types_other);

}
