package cz.frantisekmasa.wfrp_master.inventory.domain.weapon

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MeleeWeaponGroup(override val nameResolver: (strings: Strings) -> String) : NamedEnum,
    Parcelable {
    BASIC({ it.weapons.meleeGroups.basic }),
    BRAWLING({ it.weapons.meleeGroups.brawling }),
    CAVALRY({ it.weapons.meleeGroups.cavalry }),
    FENCING({ it.weapons.meleeGroups.fencing }),
    FLAIL({ it.weapons.meleeGroups.flail }),
    PARRY({ it.weapons.meleeGroups.parry }),
    POLEARM({ it.weapons.meleeGroups.polearm }),
    TWO_HANDED({ it.weapons.meleeGroups.twoHanded }),
}
