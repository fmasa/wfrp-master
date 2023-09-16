package cz.frantisekmasa.wfrp_master.common.combat.domain

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType

@Immutable
data class WornArmourPiece(
    val trapping: InventoryItem,
    val armour: TrappingType.Armour,
) {
    companion object {
        fun fromTrappingOrNull(trapping: InventoryItem): WornArmourPiece? {
            val type = trapping.trappingType

            if (type !is TrappingType.Armour || !type.worn) {
                return null
            }

            return WornArmourPiece(
                trapping = trapping,
                armour = type,
            )
        }
    }
}
