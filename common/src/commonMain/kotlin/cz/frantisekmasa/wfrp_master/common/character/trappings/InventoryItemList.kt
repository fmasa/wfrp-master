package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun InventoryItemList(
    trappings: List<TrappingsScreenModel.Trapping>,
    onClick: (InventoryItem) -> Unit,
    onRemove: (InventoryItem) -> Unit,
    onDuplicate: (InventoryItem) -> Unit,
    onAddToContainerRequest: (InventoryItem) -> Unit,
) {
    Column {
        for (trapping in trappings) {
            key(trapping.item.id) {
                Column {
                    TrappingItem(
                        trapping = trapping,
                        onClick = { onClick(trapping.item) },
                        onRemove = { onRemove(trapping.item) },
                        onDuplicate = { onDuplicate(trapping.item) },
                        additionalContextItems = listOf(
                            ContextMenu.Item(
                                LocalStrings.current.trappings.buttonMoveToContainer,
                                onClick = { onAddToContainerRequest(trapping.item) },
                            )
                        )
                    )
                    Divider()
                }
            }
        }
    }
}
