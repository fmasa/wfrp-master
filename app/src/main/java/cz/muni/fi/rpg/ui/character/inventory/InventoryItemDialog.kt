package cz.muni.fi.rpg.ui.character.inventory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemId
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import kotlinx.android.synthetic.main.inventory_item_edit_dialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryItemDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val viewModel: CharacterViewModel by activityViewModels()

    private val existingItem: InventoryItem? by lazy {
        arguments?.getParcelable<InventoryItem>(ARGUMENT_ITEM)
    }

    companion object {
        private const val ARGUMENT_ITEM = "item"

        fun newInstance(existingItem: InventoryItem?): InventoryItemDialog {
            val fragment = InventoryItemDialog()

            fragment.arguments = bundleOf(ARGUMENT_ITEM to existingItem)

            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val view = activity.layoutInflater.inflate(R.layout.inventory_item_edit_dialog, null)

        existingItem?.let {
            view.itemName.setText(it.name)
            view.itemDescription.setText(it.description)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (existingItem != null) null else getString(R.string.createInventoryItemTitle))
            .setView(view)
            .setPositiveButton(R.string.button_save, null)
            .setNeutralButton(R.string.button_cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onDialogSubmitted(view)
            }
        }

        return dialog
    }

    private fun onDialogSubmitted(view: View) {
        if (! checkItemValidity(view)) {
            return
        }

        val inventoryItem = createInventoryItem(view)

        launch {
            try {
                viewModel.saveInventoryItem(inventoryItem)

                toast(getString(R.string.inventory_toast_item_saved))
                dismiss()
            } catch (e: java.lang.Exception) {
                toast("Item couldn't be added to your inventory.")
            }
        }
    }

    private fun createInventoryItem(view: View): InventoryItem {
        val id = this.existingItem?.id ?: InventoryItemId.randomUUID()
        val name = view.itemName.text.toString().trim()
        val description = view.itemDescription.text.toString().trim()
        // TODO in next versions make editable
        val quantity = 1
        return InventoryItem(id, name, description, quantity)
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
        val name = view.itemName

        return checkEditTextValue(name, { it.text.isNotBlank() }, "Name cannot be blank.")
    }

    private fun showError(view: EditText, message: String) {
        view.error = message
    }

    private suspend fun toast(message: String) {
        withContext(Dispatchers.Main) { Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
    }
}