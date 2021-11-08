package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardItem
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardTitle
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.primitives.TopPanel
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.inventory.domain.Encumbrance
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.domain.TrappingType
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun CharacterTrappingsScreen(
    characterId: CharacterId,
    modifier: Modifier,
) {
    val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    Column(modifier.verticalScroll(rememberScrollState())) {
        TopPanel {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CharacterEncumbrance(
                    viewModel,
                    Modifier.padding(Spacing.medium),
                )

                viewModel.money.collectWithLifecycle(null).value?.let { money ->
                    var transactionDialogVisible by rememberSaveable { mutableStateOf(false) }

                    MoneyBalance(
                        money,
                        Modifier
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
            }
        }

        viewModel.armor.collectWithLifecycle(null).value?.let { armor ->
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

        val coroutineScope = rememberCoroutineScope { EmptyCoroutineContext + Dispatchers.IO }

        InventoryItemsCard(
            viewModel,
            onClick = {
                inventoryItemDialogState = DialogState.Opened(it)
            },
            onRemove = { viewModel.removeInventoryItem(it) },
            onDuplicate = { coroutineScope.launch { viewModel.saveInventoryItem(it.duplicate()) } },
            onNewItemButtonClicked = {
                inventoryItemDialogState = DialogState.Opened(null)
            },
        )

        Spacer(Modifier.padding(bottom = 20.dp))
    }
}

@Composable
private fun CharacterEncumbrance(viewModel: InventoryViewModel, modifier: Modifier) {
    val max = viewModel.maxEncumbrance.collectWithLifecycle(null).value
    val total = viewModel.totalEncumbrance.collectWithLifecycle(null).value

    val isOverburdened = max != null && total != null && total > max

    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(R.drawable.ic_encumbrance),
            stringResource(R.string.icon_total_encumbrance),
            Modifier.size(18.dp),
        )
        Text(
            "${total ?: ""} / ${max ?: "?"}",
            color = if (isOverburdened) MaterialTheme.colors.error else LocalContentColor.current
        )
    }
}

@Composable
private fun InventoryItemsCard(
    viewModel: InventoryViewModel,
    onClick: (InventoryItem) -> Unit,
    onRemove: (InventoryItem) -> Unit,
    onDuplicate: (InventoryItem) -> Unit,
    onNewItemButtonClicked: () -> Unit,
) {
    val items = viewModel.inventory.collectWithLifecycle(null).value ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp)) {
        Column(Modifier.padding(horizontal = 8.dp)) {
            CardTitle(R.string.inventory_items)
            if (items.isEmpty()) {
                EmptyUI(
                    R.string.no_inventory_item_prompt,
                    R.drawable.ic_container,
                    size = EmptyUI.Size.Small
                )
            } else {
                InventoryItemList(
                    items,
                    onClick = onClick,
                    onRemove = onRemove,
                    onDuplicate = onDuplicate
                )
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
    onDuplicate: (InventoryItem) -> Unit,
) {
    Column {
        for (item in items) {
            CardItem(
                name = item.name,
                description = item.description,
                iconRes = trappingIcon(item.trappingType),
                onClick = { onClick(item) },
                contextMenuItems = listOf(
                    ContextMenu.Item(
                        stringResource(R.string.button_duplicate),
                        onClick = { onDuplicate(item) },
                    ),
                    ContextMenu.Item(
                        stringResource(R.string.button_remove),
                        onClick = { onRemove(item) }
                    ),
                ),
                badge = {
                    val encumbrance = item.effectiveEncumbrance

                    if (encumbrance != Encumbrance.Zero) {
                        Column(horizontalAlignment = Alignment.End) {
                            if (item.quantity > 1) {
                                Text(stringResource(R.string.quantity, item.quantity))
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.tiny)
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_encumbrance),
                                    stringResource(R.string.icon_item_encumbrance),
                                    Modifier.size(Spacing.medium),
                                )
                                Text(encumbrance.toString())
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun trappingIcon(trappingType: TrappingType?) = when (trappingType) {
    is TrappingType.Ammunition -> R.drawable.ic_ammunition
    is TrappingType.Armour -> R.drawable.ic_armor_chest
    is TrappingType.MeleeWeapon -> R.drawable.ic_weapon_skill
    is TrappingType.Container -> R.drawable.ic_container
    is TrappingType.RangedWeapon -> R.drawable.ic_ballistic_skill
    null -> R.drawable.ic_miscellaneous
}
