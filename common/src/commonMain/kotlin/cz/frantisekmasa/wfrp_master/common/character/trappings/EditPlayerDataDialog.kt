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
        data =
            TrappingFromCompendiumPlayerData(
                itemQualities = trapping.itemQualities,
                itemFlaws = trapping.itemFlaws,
                quantity = trapping.quantity,
                note = trapping.note,
                isEncumbranceCounted = trapping.isEncumbranceCounted,
            ),
        onSaveRequest = {
            onSaveRequest(
                trapping.update(
                    itemQualities = it.itemQualities,
                    itemFlaws = it.itemFlaws,
                    isEncumbranceCounted = it.isEncumbranceCounted,
                    quantity = it.quantity,
                    note = it.note,
                ),
            )
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
