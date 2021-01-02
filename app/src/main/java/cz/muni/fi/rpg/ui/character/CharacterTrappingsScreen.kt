package cz.muni.fi.rpg.ui.character

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.fragmentManager
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.ui.character.inventory.ArmorCard
import cz.muni.fi.rpg.ui.character.inventory.InventoryItemDialog
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CharacterTrappingsScreen(
    characterId: CharacterId,
    modifier: Modifier,
) {
    val fragmentManager = fragmentManager()
    val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    ScrollableColumn(modifier) {
        viewModel.money.collectAsState(null).value?.let {
            CurrentMoney(
                value = it,
                onClick = {
                    TransactionDialog.newInstance(characterId).show(fragmentManager, null)
                }
            )
        }

        viewModel.armor.collectAsState(null).value?.let { armor ->
            ArmorCard(armor, onChange = { viewModel.updateArmor(it) })
        }

        InventoryItemsCard(
            viewModel,
            onClick = {
                InventoryItemDialog.newInstance(characterId, it).show(fragmentManager, null)
            },
            onRemove = { viewModel.removeInventoryItem(it) },
            onNewItemButtonClicked = {
                InventoryItemDialog.newInstance(characterId, null).show(fragmentManager, null)
            },
        )

        Spacer(Modifier.padding(bottom = 20.dp))
    }
}

@Composable
private fun CurrentMoney(value: Money, onClick: () -> Unit) {
    Row(
        Modifier
            .padding(end = 8.dp)
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
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
