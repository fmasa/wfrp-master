package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class RangedWeaponGroup() : NamedEnum, Parcelable {
    BLACKPOWDER,
    BOW,
    CROSSBOW,
    ENTANGLING,
    ENGINEERING,
    EXPLOSIVES,
    SLING,
    THROWING,
    ;

    override val translatableName: StringResource get() =
        when (this) {
            BLACKPOWDER -> Str.weapons_ranged_groups_blackpowder
            BOW -> Str.weapons_ranged_groups_bow
            CROSSBOW -> Str.weapons_ranged_groups_crossbow
            ENTANGLING -> Str.weapons_ranged_groups_entangling
            ENGINEERING -> Str.weapons_ranged_groups_engineering
            EXPLOSIVES -> Str.weapons_ranged_groups_explosives
            SLING -> Str.weapons_ranged_groups_sling
            THROWING -> Str.weapons_ranged_groups_throwing
        }
}
