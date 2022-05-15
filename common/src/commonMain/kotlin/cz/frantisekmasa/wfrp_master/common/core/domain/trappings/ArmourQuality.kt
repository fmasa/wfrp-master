package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
@Immutable
enum class ArmourQuality(
    override val nameResolver: (strings: Strings) -> String,
) : Quality {
    FLEXIBLE({ it.armour.qualities.flexible }),
    IMPENETRABLE({ it.armour.qualities.impenetrable });

    override val hasRating: Boolean get() = false
}