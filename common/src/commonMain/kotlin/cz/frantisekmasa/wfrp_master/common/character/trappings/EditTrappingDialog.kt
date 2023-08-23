package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog

@Composable
fun EditTrappingDialog(
    onSaveRequest: suspend (InventoryItem) -> Unit,
    existingItem: InventoryItem,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(
        onDismissRequest = onDismissRequest,
    ) {
        if (existingItem.compendiumId != null) {
            EditQualitiesDetailDialog(
                trapping = existingItem,
                onSaveRequest = onSaveRequest,
                onDismissRequest = onDismissRequest,
            )
        } else {
            NonCompendiumTrappingForm(
                onSaveRequest = onSaveRequest,
                existingItem = existingItem,
                defaultContainerId = existingItem.containerId,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}
