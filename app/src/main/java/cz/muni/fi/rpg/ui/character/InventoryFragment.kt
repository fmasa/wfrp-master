package cz.muni.fi.rpg.ui.character

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemId
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_inventory.view.*
import kotlinx.android.synthetic.main.inventory_item_edit_dialog.view.*
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.ui.character.adapter.InventoryAdapter
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.android.synthetic.main.fragment_inventory.*

class InventoryFragment : DaggerFragment(R.layout.fragment_inventory) {
    private var inputError = ""
    private lateinit var characterId: CharacterId
    private val viewModel: CharacterViewModel by activityViewModels()

    private fun setViewVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setEmptyCollectionView(isEmpty: Boolean) {
        setViewVisibility(noInventoryItemIcon, isEmpty)
        setViewVisibility(noInventoryItemText, isEmpty)
        setViewVisibility(inventoryRecycler, !isEmpty)
    }

    private fun showDialog(){
        val view = requireActivity().layoutInflater.inflate(R.layout.inventory_item_edit_dialog, null)
        AlertDialog.Builder(activity)
            .setTitle(R.string.createInventoryItemTitle)
            .setView(view)
            .setPositiveButton(R.string.createInventoryItemSubmit) { _, _ -> onNewItemSubmited(view) }
            .setNeutralButton(R.string.createInventoryItemCancel) { _, _ -> }
            .create()
            .show()
    }

    private fun onNewItemSubmited(view: View) {
        val name = view.newInventoryItemName.text.toString().trim()
        val description = view.newInventoryItemDescription.text.toString().trim()
        val quantity = 1

        if (checkItemValidity(name, description, quantity)){
            val inventoryItem = InventoryItem(InventoryItemId.randomUUID(), name,  description, quantity)
            viewModel.saveInventoryItem(inventoryItem)
            // TODO Extract to resources
            Toast.makeText(context, "Item '${inventoryItem.name}' was added to your inventory.", Toast.LENGTH_LONG).show()
        }
        else{
            showError()
        }
    }

    private fun showError() {
        Toast.makeText(context, inputError, Toast.LENGTH_LONG).show()
    }

    private fun checkItemValidity(name: String, description: String, quantity: Int): Boolean {
        if(name.isBlank()) {
            inputError = "Name cannot be blank."
            return false
        } else if (quantity <= 0) {
            inputError = "Quantity must be >= 0."
            return false
        }

        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
	super.onViewCreated(view, savedInstanceState)

	Transformations.map(viewModel.character.right()) { character -> character.getMoney() }
            .observe(viewLifecycleOwner, characterMoney::setValue)

        characterMoney.setOnClickListener {
            TransactionDialog(viewModel).show(parentFragmentManager, "TransactionDialog")
        }

        view.addNewInventoryItemButton.setOnClickListener(){ showDialog() }

        val adapter = InventoryAdapter(layoutInflater)
        inventoryRecycler.layoutManager = LinearLayoutManager(context)
        inventoryRecycler.adapter = adapter

        viewModel.inventory.observe(this) { items->
            adapter.submitList(items)
            setEmptyCollectionView(items.isEmpty())
        }
    }
}
