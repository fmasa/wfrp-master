package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class ArmourFlaw(
    override val translatableName: StringResource
) : Flaw {
    PARTIAL(Str.armour_flaws_partial),
    WEAKPOINTS(Str.armour_flaws_weakpoints);

    override val hasRating: Boolean get() = false
}
