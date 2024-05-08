package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@Immutable
enum class WeaponEquip : NamedEnum, Parcelable {
    BOTH_HANDS,
    PRIMARY_HAND,
    OFF_HAND,
    ;

    override val translatableName get() =
        when (this) {
            BOTH_HANDS -> Str.weapons_equip_both_hands
            PRIMARY_HAND -> Str.weapons_equip_primary_hand
            OFF_HAND -> Str.weapons_equip_off_hand
        }
}
