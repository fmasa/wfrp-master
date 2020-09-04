package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.adapter.InventoryAdapter
import cz.muni.fi.rpg.ui.character.inventory.ArmorCard
import cz.muni.fi.rpg.ui.character.inventory.InventoryItemDialog
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.ui.common.NonScrollableLayoutManager
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.ui.views.MoneyView
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
            MainContainer(viewModel, this)
        }

        val characterMoney = view.findViewById<MoneyView>(R.id.characterMoney)
        viewModel.money.observe(viewLifecycleOwner, characterMoney::setValue)

        characterMoney.setOnClickListener {
            TransactionDialog.newInstance(characterId).show(parentFragmentManager, null)
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
private fun MainContainer(viewModel: InventoryViewModel, coroutineScope: CoroutineScope) {
    val armor = viewModel.armor.right().observeAsState().value ?: return

    Column {
        ArmorCard(armor, onChange = { coroutineScope.launch { viewModel.updateArmor(it) } })
    }
}
