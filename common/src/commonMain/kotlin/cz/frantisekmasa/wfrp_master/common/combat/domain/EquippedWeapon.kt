package cz.frantisekmasa.wfrp_master.common.combat.domain

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.Damage
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponEquip

@Immutable
data class EquippedWeapon(
    val weapon: TrappingType.Weapon,
    val trapping: InventoryItem,
    val equip: WeaponEquip,
    val damage: Damage,
) {
    companion object {
        fun fromTrappingOrNull(trapping: InventoryItem, strengthBonus: Int): EquippedWeapon? {
            val type = trapping.trappingType

            if (type !is TrappingType.Weapon) {
                return null
            }

            val equip = type.equipped ?: return null

            return EquippedWeapon(
                trapping = trapping,
                weapon = type,
                equip = equip,
                damage = type.damage.calculate(
                    strengthBonus = strengthBonus,
                    successLevels = 0,
                )
            )
        }
    }
}
