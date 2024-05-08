package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import kotlinx.collections.immutable.ImmutableList

data class TrappingsScreenState(
    val currentEncumbrance: Encumbrance,
    val maxEncumbrance: Encumbrance,
    val money: Money,
    val trappings: ImmutableList<TrappingItem>,
)

@Immutable
sealed interface TrappingItem {
    val item: InventoryItem
    val allItems: List<InventoryItem>

    @Immutable
    data class Container(
        override val item: InventoryItem,
        val container: TrappingType.Container,
        val storedTrappings: List<InventoryItem>,
    ) : TrappingItem {
        override val allItems: List<InventoryItem> = listOf(item) + storedTrappings
    }

    @Immutable
    data class SeparateTrapping(override val item: InventoryItem) : TrappingItem {
        override val allItems: List<InventoryItem> get() = listOf(item)
    }
}
