package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
@Immutable
enum class HitLocation(
    override val nameResolver: (strings: Strings) -> String,
) : NamedEnum, Parcelable {
    HEAD({ it.combat.hitLocations.head }),
    BODY({ it.combat.hitLocations.body }),
    LEFT_ARM({ it.combat.hitLocations.leftArm }),
    RIGHT_ARM({ it.combat.hitLocations.rightArm }),
    LEFT_LEG({ it.combat.hitLocations.leftLeg }),
    RIGHT_LEG({ it.combat.hitLocations.rightLeg });

    val rollRange: IntRange get() = when (this) {
        HEAD -> 1..9
        LEFT_ARM -> 10..24
        RIGHT_ARM -> 25..44
        BODY -> 45..79
        LEFT_LEG -> 80..89
        RIGHT_LEG -> 90..100
    }
}
