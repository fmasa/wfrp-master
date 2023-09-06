package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class WeaponEquip(
    override val translatableName: StringResource,
) : NamedEnum, Parcelable {
    BOTH_HANDS(Str.weapons_equip_both_hands),
    PRIMARY_HAND(Str.weapons_equip_primary_hand),
    OFF_HAND(Str.weapons_equip_off_hand),
}
