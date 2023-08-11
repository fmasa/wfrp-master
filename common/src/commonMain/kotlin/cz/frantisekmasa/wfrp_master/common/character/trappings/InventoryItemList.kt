package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import dev.icerock.moko.resources.compose.stringResource

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
                                stringResource(Str.trappings_button_move_to_container),
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
