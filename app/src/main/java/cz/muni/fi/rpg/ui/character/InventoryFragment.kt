package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import cz.muni.fi.rpg.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_inventory.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.adapter.InventoryAdapter
import cz.muni.fi.rpg.ui.character.inventory.InventoryItemDialog
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.android.synthetic.main.fragment_inventory.*
import kotlinx.coroutines.*

class InventoryFragment : DaggerFragment(R.layout.fragment_inventory),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val viewModel: CharacterViewModel by activityViewModels()

    private fun setViewVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setEmptyCollectionView(isEmpty: Boolean) {
        setViewVisibility(noInventoryItemIcon, isEmpty)
        setViewVisibility(noInventoryItemText, isEmpty)
        setViewVisibility(inventoryRecycler, !isEmpty)
    }

    private fun showDialog() {
        InventoryItemDialog().show(childFragmentManager, "InventoryItemDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Transformations.map(viewModel.character.right()) { character -> character.getMoney() }
            .observe(viewLifecycleOwner, characterMoney::setValue)

        characterMoney.setOnClickListener {
            TransactionDialog(viewModel).show(parentFragmentManager, "TransactionDialog")
        }

        view.addNewInventoryItemButton.setOnClickListener() { showDialog() }

        val adapter = InventoryAdapter(
            layoutInflater,
            onRemoveListener = { launch { viewModel.removeInventoryItem(it) } }
        )
        inventoryRecycler.layoutManager = LinearLayoutManager(context)
        inventoryRecycler.adapter = adapter

        viewModel.inventory.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            setEmptyCollectionView(items.isEmpty())
        }
    }
}
