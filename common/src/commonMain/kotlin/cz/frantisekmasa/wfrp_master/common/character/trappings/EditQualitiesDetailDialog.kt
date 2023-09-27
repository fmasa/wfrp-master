package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem

@Composable
fun EditQualitiesDetailDialog(
    trapping: InventoryItem,
    onSaveRequest: suspend (InventoryItem) -> Unit,
    onDismissRequest: () -> Unit,
) {
    TrappingFromCompendiumForm(
        itemName = trapping.name,
        itemQualities = trapping.itemQualities,
        itemFlaws = trapping.itemFlaws,
        quantity = trapping.quantity,
        onSaveRequest = { qualities, flaws, quantity ->
            onSaveRequest(
                trapping.update(
                    itemQualities = qualities,
                    itemFlaws = flaws,
                    quantity = quantity,
                )
            )
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
