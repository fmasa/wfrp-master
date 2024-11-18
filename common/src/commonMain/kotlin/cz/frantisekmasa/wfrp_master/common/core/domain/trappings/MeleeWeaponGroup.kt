package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class MeleeWeaponGroup(
    /**
     * If true, there will be a link to Journal entry with special rules for this group.
     */
    val hasSpecialRules: Boolean = false,
) : NamedEnum, Parcelable {
    BASIC,
    BRAWLING,
    CAVALRY(hasSpecialRules = true),
    FENCING,
    FLAIL(hasSpecialRules = true),
    PARRY(hasSpecialRules = true),
    POLEARM,
    TWO_HANDED,
    ;

    override val translatableName: StringResource get() =
        when (this) {
            BASIC -> Str.weapons_melee_groups_basic
            BRAWLING -> Str.weapons_melee_groups_brawling
            CAVALRY -> Str.weapons_melee_groups_cavalry
            FENCING -> Str.weapons_melee_groups_fencing
            FLAIL -> Str.weapons_melee_groups_flail
            PARRY -> Str.weapons_melee_groups_parry
            POLEARM -> Str.weapons_melee_groups_polearm
            TWO_HANDED -> Str.weapons_melee_groups_two_handed
        }
}
