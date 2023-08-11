package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class ArmourQuality(
    override val translatableName: StringResource,
) : Quality {
    FLEXIBLE(Str.armour_qualities_flexible),
    IMPENETRABLE(Str.armour_qualities_impenetrable);

    override val hasRating: Boolean get() = false
}
