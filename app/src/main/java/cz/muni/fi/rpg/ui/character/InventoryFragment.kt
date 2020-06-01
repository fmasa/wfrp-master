package cz.muni.fi.rpg.ui.character

import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemId
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_inventory.view.*
import kotlinx.android.synthetic.main.inventory_item_edit_dialog.view.*
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.character.adapter.InventoryAdapter
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.android.synthetic.main.fragment_inventory.*
import kotlinx.coroutines.*

class InventoryFragment : DaggerFragment(R.layout.fragment_inventory), CoroutineScope by CoroutineScope(Dispatchers.Default) {
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
        val activity = requireActivity()
        val view = activity.layoutInflater.inflate(R.layout.inventory_item_edit_dialog, null)
        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.createInventoryItemTitle)
            .setView(view)
            .setPositiveButton(R.string.button_save, null)
            .setNeutralButton(R.string.button_cancel, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onNewItemSubmitted(view, dialog)
            }
        }
        dialog.show()
    }

    private fun onNewItemSubmitted(view: View, dialog: AlertDialog) {
        try {
            if (checkItemValidity(view)) {
                val inventoryItem = createInventoryItem(view)
                launch {
                    viewModel.saveInventoryItem(inventoryItem)

                    withContext(Dispatchers.Main) {
                        // TODO Extract to resources
                        Toast.makeText(
                            context,
                            "Item '${inventoryItem.name}' was added to your inventory.",
                            Toast.LENGTH_LONG
                        ).show()

                        dialog.dismiss()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Item couldn't be added to your inventory.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun createInventoryItem(view: View): InventoryItem {
        val id = InventoryItemId.randomUUID()
        val name = view.newInventoryItemName.text.toString().trim()
        val description = view.newInventoryItemDescription.text.toString().trim()
        // TODO in next versions make editable
        val quantity = 1
        return InventoryItem(id, name, description, quantity)
    }

    private fun showError(view: EditText, message: String) {
        view.error = message
    }

    private fun checkEditTextValue(
        view: EditText,
        predicate: (EditText) -> Boolean,
        message: String
    ): Boolean {
        if (!predicate(view)) {
            showError(view, message)
            return false
        }
        return true
    }

    private fun checkItemValidity(view: View): Boolean {
        val name = view.newInventoryItemName

        return checkEditTextValue(name, { it.text.isNotBlank() }, "Name cannot be blank.")
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
