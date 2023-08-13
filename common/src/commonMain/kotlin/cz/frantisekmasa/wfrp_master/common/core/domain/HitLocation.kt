package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class HitLocation(
    override val translatableName: StringResource,
) : NamedEnum, Parcelable {
    HEAD(Str.combat_hit_locations_head),
    BODY(Str.combat_hit_locations_body),
    LEFT_ARM(Str.combat_hit_locations_left_arm),
    RIGHT_ARM(Str.combat_hit_locations_right_arm),
    LEFT_LEG(Str.combat_hit_locations_left_leg),
    RIGHT_LEG(Str.combat_hit_locations_right_leg);

    val rollRange: IntRange get() = when (this) {
        HEAD -> 1..9
        LEFT_ARM -> 10..24
        RIGHT_ARM -> 25..44
        BODY -> 45..79
        LEFT_LEG -> 80..89
        RIGHT_LEG -> 90..100
    }
}
