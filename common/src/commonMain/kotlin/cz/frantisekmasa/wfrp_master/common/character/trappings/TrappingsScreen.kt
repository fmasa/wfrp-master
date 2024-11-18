package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.characterItemsCard
import cz.frantisekmasa.wfrp_master.common.character.trappings.add.AddTrappingScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopPanel
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TrappingsScreen(
    characterId: CharacterId,
    onMoneyBalanceUpdate: suspend (Money) -> Unit,
    onAddToContainer: suspend (trapping: InventoryItem, container: InventoryItem) -> Unit,
    onRemove: (InventoryItem) -> Unit,
    onDuplicate: (InventoryItem) -> Unit,
    state: TrappingsScreenState,
    modifier: Modifier,
) {
    var addToContainerDialogTrapping: InventoryItem?
        by remember { mutableStateOf(null) }

    addToContainerDialogTrapping?.let { trapping ->
        val containers by derivedStateOf {
            state.trappings.filter { it.item.id != trapping.id && it is TrappingItem.Container }
        }

        ChooseTrappingDialog(
            title = stringResource(Str.trappings_title_select_container),
            trappings = containers,
            onSelected = { onAddToContainer(trapping, it.item) },
            emptyUiText = stringResource(Str.trappings_messages_no_containers_found),
            onDismissRequest = { addToContainerDialogTrapping = null },
        )
    }

    LazyColumn(modifier.fillMaxSize()) {
        item("top-panel") {
            TopPanel {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    CharacterEncumbrance(
                        current = state.currentEncumbrance,
                        max = state.maxEncumbrance,
                        Modifier.padding(Spacing.medium),
                    )

                    var transactionDialogVisible by rememberSaveable { mutableStateOf(false) }

                    MoneyBalance(
                        state.money,
                        Modifier
                            .clickable { transactionDialogVisible = true }
                            .padding(Spacing.medium)
                            .padding(end = 8.dp),
                    )

                    if (transactionDialogVisible) {
                        TransactionDialog(
                            state.money,
                            updateBalance = {
                                onMoneyBalanceUpdate(it)
                                transactionDialogVisible = false
                            },
                            onDismissRequest = { transactionDialogVisible = false },
                        )
                    }
                }
            }
        }

        characterItemsCard(
            leadingDivider = true,
            title = { stringResource(Str.trappings_title) },
            key = "trappings",
            id = { it.item.id },
            items = state.trappings,
            newItemScreen = { AddTrappingScreen(characterId, containerId = null) },
            noItems = {
                EmptyUI(
                    stringResource(Str.trappings_messages_no_items),
                    Resources.Drawable.TrappingContainer,
                    size = EmptyUI.Size.Small,
                )
            },
            detailScreen = { trapping ->
                CharacterTrappingDetailScreen(
                    characterId,
                    trapping.item.id,
                )
            },
            onRemove = { onRemove(it.item) },
            contextMenuItems = {
                listOf(
                    ContextMenu.Item(
                        stringResource(Str.trappings_button_move_to_container),
                        onClick = { addToContainerDialogTrapping = it.item },
                    ),
                    ContextMenu.Item(
                        stringResource(Str.common_ui_button_duplicate),
                        onClick = { onDuplicate(it.item) },
                    ),
                )
            },
            item = { trapping -> TrappingItem(trapping) },
        )
    }
}

@Composable
private fun CharacterEncumbrance(
    current: Encumbrance,
    max: Encumbrance,
    modifier: Modifier,
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            drawableResource(Resources.Drawable.TrappingEncumbrance),
            stringResource(Str.trappings_icon_total_encumbrance),
            Modifier.size(18.dp),
        )
        Text(
            "$current / $max",
            color = if (current > max) MaterialTheme.colors.error else LocalContentColor.current,
        )
    }
}
