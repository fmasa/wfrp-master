package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem

@Composable
fun EditTrappingDialog(
    onSaveRequest: suspend (InventoryItem) -> Unit,
    existingItem: InventoryItem,
    onDismissRequest: () -> Unit,
) {
    NonCompendiumTrappingForm(
        onSaveRequest = onSaveRequest,
        existingItem = existingItem,
        defaultContainerId = existingItem.containerId,
        onDismissRequest = onDismissRequest,
    )
}
