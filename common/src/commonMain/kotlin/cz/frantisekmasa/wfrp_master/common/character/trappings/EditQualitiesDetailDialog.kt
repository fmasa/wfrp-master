package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem

@Composable
fun EditQualitiesDetailDialog(
    trapping: InventoryItem,
    onSaveRequest: suspend (InventoryItem) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ItemQualitiesAndFlawsForm(
        itemName = trapping.name,
        itemQualities = trapping.itemQualities,
        itemFlaws = trapping.itemFlaws,
        onSaveRequest = { qualities, flaws ->
            onSaveRequest(trapping.updateItemQualitiesAndFlaws(qualities, flaws))
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
