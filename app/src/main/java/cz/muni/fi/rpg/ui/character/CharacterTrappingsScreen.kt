package cz.muni.fi.rpg.ui.character

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.fragmentManager
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.ui.character.inventory.ArmorCard
import cz.muni.fi.rpg.ui.character.inventory.InventoryItemDialog
import cz.muni.fi.rpg.ui.character.inventory.MoneyBalance
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CharacterTrappingsScreen(
    characterId: CharacterId,
    modifier: Modifier,
) {
    val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    ScrollableColumn(modifier) {
        viewModel.money.collectAsState(null).value?.let { money ->
            var transactionDialogVisible by savedInstanceState { false }

            MoneyBalance(
                money,
                Modifier
                    .fillMaxWidth()
                    .clickable { transactionDialogVisible = true }
                    .padding(Spacing.medium)
                    .padding(end = 8.dp),
            )

            if (transactionDialogVisible) {
                TransactionDialog(
                    money,
                    viewModel,
                    onDismissRequest = { transactionDialogVisible = false },
                )
            }
        }

        viewModel.armor.collectAsState(null).value?.let { armor ->
            ArmorCard(armor, onChange = { viewModel.updateArmor(it) })
        }

        var inventoryItemDialogState: DialogState<InventoryItem?> by remember {
            mutableStateOf(DialogState.Closed())
        }

        val dialogState = inventoryItemDialogState

        if (dialogState is DialogState.Opened) {
            InventoryItemDialog(
                viewModel = viewModel,
                existingItem = dialogState.item,
                onDismissRequest = { inventoryItemDialogState = DialogState.Closed() }
            )
        }

        InventoryItemsCard(
            viewModel,
            onClick = {
                inventoryItemDialogState = DialogState.Opened(it)
            },
            onRemove = { viewModel.removeInventoryItem(it) },
            onNewItemButtonClicked = {
                inventoryItemDialogState = DialogState.Opened(null)
            },
        )

        Spacer(Modifier.padding(bottom = 20.dp))
    }
}

@Composable
private fun InventoryItemsCard(
    viewModel: InventoryViewModel,
    onClick: (InventoryItem) -> Unit,
    onRemove: (InventoryItem) -> Unit,
    onNewItemButtonClicked: () -> Unit,
) {
    val items = viewModel.inventory.collectAsState(null).value ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp)) {
        Column(Modifier.padding(horizontal = 8.dp)) {
            CardTitle(R.string.title_character_trappings)
            if (items.isEmpty()) {
                EmptyUI(
                    R.string.no_inventory_item_prompt,
                    R.drawable.ic_inventory,
                    size = EmptyUI.Size.Small
                )
            } else {
                InventoryItemList(items, onClick = onClick, onRemove = onRemove)
            }

            CardButton(R.string.title_inventory_add_item, onClick = onNewItemButtonClicked)
        }
    }
}

@Composable
private fun InventoryItemList(
    items: List<InventoryItem>,
    onClick: (InventoryItem) -> Unit,
    onRemove: (InventoryItem) -> Unit,
) {
    Column {

        for (item in items) {
            CardItem(
                name = item.name,
                description = item.description,
                iconRes = R.drawable.ic_inventory,
                onClick = { onClick(item) },
                contextMenuItems = listOf(
                    ContextMenu.Item(stringResource(R.string.remove), onClick = { onRemove(item) })
                ),
                badge = {
                    if (item.quantity > 1) {
                        Text(stringResource(R.string.quantity, item.quantity))
                    }
                }
            )
        }
    }
}
