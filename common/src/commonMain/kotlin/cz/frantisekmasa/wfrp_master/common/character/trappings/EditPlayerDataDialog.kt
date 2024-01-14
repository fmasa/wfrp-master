package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem

@Composable
fun EditPlayerDataDialog(
    trapping: InventoryItem,
    onSaveRequest: suspend (InventoryItem) -> Unit,
    onDismissRequest: () -> Unit,
) {
    TrappingFromCompendiumForm(
        itemName = trapping.name,
        data = TrappingFromCompendiumPlayerData(
            itemQualities = trapping.itemQualities,
            itemFlaws = trapping.itemFlaws,
            quantity = trapping.quantity,
            note = trapping.note,
        ),
        onSaveRequest = {
            onSaveRequest(
                trapping.update(
                    itemQualities = it.itemQualities,
                    itemFlaws = it.itemFlaws,
                    quantity = it.quantity,
                    note = it.note,
                )
            )
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
