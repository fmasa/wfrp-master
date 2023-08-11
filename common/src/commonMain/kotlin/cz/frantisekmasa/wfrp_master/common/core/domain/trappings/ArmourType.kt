package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class ArmourType(
    override val translatableName: StringResource,
) : NamedEnum, Parcelable {
    SOFT_LEATHER(Str.armour_types_soft_leather),
    BOILED_LEATHER(Str.armour_types_boiled_leather),
    MAIL(Str.armour_types_mail),
    PLATE(Str.armour_types_plate),
    OTHER(Str.armour_types_other),
}
