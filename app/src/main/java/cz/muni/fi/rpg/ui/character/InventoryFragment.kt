package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.gravity
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
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.common.Money
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.adapter.InventoryAdapter
import cz.muni.fi.rpg.ui.character.inventory.ArmorCard
import cz.muni.fi.rpg.ui.character.inventory.InventoryItemDialog
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.ui.common.NonScrollableLayoutManager
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class InventoryFragment : Fragment(R.layout.fragment_inventory),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = InventoryFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    private val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    private fun setEmptyCollectionView(view: View, isEmpty: Boolean) {
        view.findViewById<View>(R.id.noInventoryItemIcon).toggleVisibility(isEmpty)
        view.findViewById<View>(R.id.noInventoryItemText).toggleVisibility(isEmpty)
        view.findViewById<View>(R.id.inventoryRecycler).toggleVisibility(!isEmpty)
    }

    private fun showDialog(existingItem: InventoryItem?) {
        InventoryItemDialog.newInstance(characterId, existingItem)
            .show(childFragmentManager, "InventoryItemDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ComposeView>(R.id.compose).setContent {
            MainContainer(
                viewModel = viewModel,
                coroutineScope = this,
                onMoneyChangeRequest = {
                    TransactionDialog.newInstance(characterId).show(parentFragmentManager, null)
                },
            )
        }

        view.findViewById<View>(R.id.addNewInventoryItemButton)
            .setOnClickListener { showDialog(null) }

        val adapter = InventoryAdapter(
            layoutInflater,
            onClickListener = this::showDialog,
            onRemoveListener = { launch { viewModel.removeInventoryItem(it) } }
        )

        val inventoryRecycler = view.findViewById<RecyclerView>(R.id.inventoryRecycler)
        inventoryRecycler.layoutManager = NonScrollableLayoutManager(requireContext())
        inventoryRecycler.adapter = adapter

        viewModel.inventory.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            setEmptyCollectionView(view, items.isEmpty())
        }
    }
}

@Composable
private fun MainContainer(
    viewModel: InventoryViewModel,
    coroutineScope: CoroutineScope,
    onMoneyChangeRequest: () -> Unit
) {
    Column {
        viewModel.money.observeAsState().value?.let {
            CurrentMoney(value = it, onClick = onMoneyChangeRequest)
        }

        viewModel.armor.right().observeAsState().value ?.let {
            ArmorCard(it, onChange = { coroutineScope.launch { viewModel.updateArmor(it) } })
        }
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
