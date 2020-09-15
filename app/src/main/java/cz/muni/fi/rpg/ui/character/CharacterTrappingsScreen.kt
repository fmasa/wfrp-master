package cz.muni.fi.rpg.ui.character

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.Money
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.inventory.ArmorCard
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.InventoryViewModel

@Composable
fun CharacterTrappingsScreen(
    modifier: Modifier,
    viewModel: InventoryViewModel,
    onMoneyDialogRequest: () -> Unit,
    onItemDialogRequest: (InventoryItem?) -> Unit,
) {
    ScrollableColumn(modifier) {
        viewModel.money.observeAsState().value?.let {
            CurrentMoney(value = it, onClick = onMoneyDialogRequest)
        }

        viewModel.armor.right().observeAsState().value?.let { armor ->
            ArmorCard(armor, onChange = { viewModel.updateArmor(it) })
        }

        InventoryItemsCard(
            viewModel,
            onClick = onItemDialogRequest,
            onRemove = { viewModel.removeInventoryItem(it) },
            onNewItemButtonClicked = { onItemDialogRequest(null) },
        )

        Spacer(Modifier.padding(bottom = 20.dp))
    }
}

@Composable
private fun CurrentMoney(value: Money, onClick: () -> Unit) {
    Row(
        Modifier.gravity(Alignment.End)
            .padding(end = 8.dp)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProvideTextStyle(MaterialTheme.typography.body1) {
            MoneyIcon(Theme.fixedColors.currencyGold)
            Text(value.getCrowns().toString() + " " + stringResource(R.string.gold_coins_shortcut))

            MoneyIcon(Theme.fixedColors.currencySilver)
            Text(value.getShillings().toString() + " " + stringResource(R.string.silver_shillings_shortcut))

            MoneyIcon(Theme.fixedColors.currencyBrass)
            Text(value.getPennies().toString() + " " + stringResource(R.string.brass_pennies_shortcut))
        }
    }
}

@Composable
private fun MoneyIcon(tint: Color) {
    Icon(
        vectorResource(R.drawable.ic_coins),
        tint = tint,
        modifier = Modifier.size(18.dp)
    )
}

@Composable
private fun InventoryItemsCard(
    viewModel: InventoryViewModel,
    onClick: (InventoryItem) -> Unit,
    onRemove: (InventoryItem) -> Unit,
    onNewItemButtonClicked: () -> Unit,
) {
    val items = viewModel.inventory.observeAsState().value ?: return

    CardContainer(Modifier.padding(horizontal = 8.dp)) {
        Column(Modifier.padding(horizontal = 8.dp)) {
            CardTitle(R.string.title_character_trappings)
            if (items.isEmpty()) {
                EmptyUI(
                    R.string.no_inventory_item_prompt,
                    R.drawable.ic_inventory,
                    EmptyUI.Size.Small
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
    LazyColumnFor(items) { item ->
        CardItem(
            name = item.name,
            description = item.description,
            iconRes = R.drawable.ic_inventory,
            onClick = { onClick(item) },
            contextMenuItems = listOf(
                ContextMenu.Item(stringResource(R.string.remove), onClick = { onRemove(item) })
            ),
            badgeContent = {
                if (item.quantity > 1) {
                    Text(stringResource(R.string.quantity, item.quantity))
                }
            }
        )
    }
}
