package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class RangedWeaponGroup(
    override val translatableName: StringResource,
    val needsAmmo: Boolean = true
) : NamedEnum, Parcelable {
    BLACKPOWDER(Str.weapons_ranged_groups_blackpowder),
    BOW(Str.weapons_ranged_groups_bow),
    CROSSBOW(Str.weapons_ranged_groups_crossbow),
    ENTANGLING(Str.weapons_ranged_groups_entangling),
    ENGINEERING(Str.weapons_ranged_groups_engineering),
    EXPLOSIVES(Str.weapons_ranged_groups_explosives),
    SLING(Str.weapons_ranged_groups_sling),
    THROWING(Str.weapons_ranged_groups_throwing, needsAmmo = false),
}
