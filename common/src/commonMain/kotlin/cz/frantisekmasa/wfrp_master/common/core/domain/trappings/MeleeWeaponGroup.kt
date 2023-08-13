package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class MeleeWeaponGroup(
    override val translatableName: StringResource,
) : NamedEnum, Parcelable {
    BASIC(Str.weapons_melee_groups_basic),
    BRAWLING(Str.weapons_melee_groups_brawling),
    CAVALRY(Str.weapons_melee_groups_cavalry),
    FENCING(Str.weapons_melee_groups_fencing),
    FLAIL(Str.weapons_melee_groups_flail),
    PARRY(Str.weapons_melee_groups_parry),
    POLEARM(Str.weapons_melee_groups_polearm),
    TWO_HANDED(Str.weapons_melee_groups_two_handed),
}
