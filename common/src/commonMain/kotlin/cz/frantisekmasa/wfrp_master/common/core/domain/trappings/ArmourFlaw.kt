package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
@Immutable
enum class ArmourFlaw(
    override val nameResolver: (strings: Strings) -> String,
) : Flaw {
    PARTIAL({ it.armour.flaws.partial }),
    WEAKPOINTS({ it.armour.flaws.weakpoints });

    override val hasRating: Boolean = false
}