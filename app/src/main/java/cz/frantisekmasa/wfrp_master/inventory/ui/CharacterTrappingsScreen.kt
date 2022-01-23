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
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.TopPanel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.inventory.domain.Encumbrance
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.domain.TrappingType
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
            drawableResource(Resources.Drawable.TrappingEncumbrance),
            LocalStrings.current.trappings.iconTotalEncumbrance,
            Modifier.size(18.dp),
        )
        Text(
            "${total ?: "?"} / ${max ?: "?"}",
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

    val strings = LocalStrings.current.trappings

    CardContainer(Modifier.padding(horizontal = 8.dp)) {
        Column(Modifier.padding(horizontal = 8.dp)) {
            CardTitle(strings.title)
            if (items.isEmpty()) {
                EmptyUI(
                    text = strings.messages.noItems,
                    Resources.Drawable.TrappingContainer,
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

            CardButton(strings.titleAdd, onClick = onNewItemButtonClicked)
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
        val strings = LocalStrings.current
        for (item in items) {
            CardItem(
                name = item.name,
                description = item.description,
                icon = { ItemIcon(trappingIcon(item.trappingType), ItemIcon.Size.Small) },
                onClick = { onClick(item) },
                contextMenuItems = listOf(
                    ContextMenu.Item(
                        strings.commonUi.buttonDuplicate,
                        onClick = { onDuplicate(item) },
                    ),
                    ContextMenu.Item(
                        strings.commonUi.buttonRemove,
                        onClick = { onRemove(item) }
                    ),
                ),
                badge = {
                    val encumbrance = item.effectiveEncumbrance

                    if (encumbrance != Encumbrance.Zero) {
                        Column(horizontalAlignment = Alignment.End) {
                            if (item.quantity > 1) {
                                Text("× ${item.quantity}")
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.tiny)
                            ) {
                                Icon(
                                    drawableResource(Resources.Drawable.TrappingEncumbrance),
                                    strings.trappings.iconEncumbrance,
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
    is TrappingType.Ammunition -> Resources.Drawable.TrappingAmmunition
    is TrappingType.Armour -> Resources.Drawable.ArmorChest
    is TrappingType.MeleeWeapon -> Resources.Drawable.WeaponSkill
    is TrappingType.Container -> Resources.Drawable.TrappingContainer
    is TrappingType.RangedWeapon -> Resources.Drawable.BallisticSkill
    null -> Resources.Drawable.TrappingMiscellaneous
}
