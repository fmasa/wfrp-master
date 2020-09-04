package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.common.Money
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.inventory.ArmorCard
import cz.muni.fi.rpg.ui.character.inventory.InventoryItemDialog
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class InventoryFragment : Fragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = InventoryFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    private val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Theme {
                MainContainer(
                    viewModel = viewModel,
                    coroutineScope = this@InventoryFragment,
                    onMoneyChangeRequest = {
                        TransactionDialog.newInstance(characterId).show(parentFragmentManager, null)
                    },
                    onItemClicked = ::showDialog,
                    onNewItemButtonClicked = { showDialog(null) }
                )
            }
        }
    }

    private fun showDialog(existingItem: InventoryItem?) {
        InventoryItemDialog.newInstance(characterId, existingItem)
            .show(childFragmentManager, "InventoryItemDialog")
    }
}

@Composable
private fun MainContainer(
    viewModel: InventoryViewModel,
    coroutineScope: CoroutineScope,
    onMoneyChangeRequest: () -> Unit,
    onItemClicked: (InventoryItem) -> Unit,
    onNewItemButtonClicked: () -> Unit,
) {
    ScrollableColumn {
        viewModel.money.observeAsState().value?.let {
            CurrentMoney(value = it, onClick = onMoneyChangeRequest)
        }

        viewModel.armor.right().observeAsState().value?.let {
            ArmorCard(it, onChange = { coroutineScope.launch { viewModel.updateArmor(it) } })
        }

        InventoryItemsCard(
            viewModel,
            onClick = onItemClicked,
            onRemove = { coroutineScope.launch { viewModel.removeInventoryItem(it) } },
            onNewItemButtonClicked = onNewItemButtonClicked,
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
            MoneyIcon(R.color.colorGold)
            Text(
                value.getCrowns()
                    .toString() + " " + stringResource(R.string.gold_coins_shortcut)
            )

            MoneyIcon(R.color.colorSilver)
            Text(
                value.getShillings()
                    .toString() + " " + stringResource(R.string.silver_shillings_shortcut)
            )

            MoneyIcon(R.color.colorBrass)
            Text(
                value.getPennies()
                    .toString() + " " + stringResource(R.string.brass_pennies_shortcut)
            )
        }
    }
}

@Composable
private fun MoneyIcon(@ColorRes tint: Int) {
    Icon(
        vectorResource(R.drawable.ic_coins),
        tint = colorResource(tint),
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
