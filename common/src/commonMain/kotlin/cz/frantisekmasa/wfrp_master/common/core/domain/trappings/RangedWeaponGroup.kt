package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Parcelize
@Immutable
enum class RangedWeaponGroup(
    override val nameResolver: (strings: Strings) -> String,
    val needsAmmo: Boolean = true
) : NamedEnum, Parcelable {
    BLACKPOWDER({ it.weapons.rangedGroups.blackpowder }),
    BOW({ it.weapons.rangedGroups.bow }),
    CROSSBOW({ it.weapons.rangedGroups.crossbow }),
    ENTANGLING({ it.weapons.rangedGroups.entangling }),
    ENGINEERING({ it.weapons.rangedGroups.engineering }),
    EXPLOSIVES({ it.weapons.rangedGroups.explosives }),
    SLING({ it.weapons.rangedGroups.sling }),
    THROWING({ it.weapons.rangedGroups.throwing }, needsAmmo = false),
}
