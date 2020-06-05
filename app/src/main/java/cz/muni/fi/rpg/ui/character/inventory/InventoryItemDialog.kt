package cz.muni.fi.rpg.ui.character.inventory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemId
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.android.synthetic.main.inventory_item_edit_dialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class InventoryItemDialog : DialogFragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val existingItem: InventoryItem? by lazy {
        requireArguments().getParcelable<InventoryItem>(ARGUMENT_ITEM)
    }
    private val characterId: CharacterId by lazy {
        requireNotNull(requireArguments().getParcelable<CharacterId>(ARGUMENT_CHARACTER_ID))
    }

    private val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    companion object {
        private const val ARGUMENT_ITEM = "item"
        private const val ARGUMENT_CHARACTER_ID = "characterId"

        fun newInstance(
            characterId: CharacterId,
            existingItem: InventoryItem?
        ) = InventoryItemDialog().apply {
            arguments = bundleOf(
                ARGUMENT_CHARACTER_ID to characterId,
                ARGUMENT_ITEM to existingItem
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val view = activity.layoutInflater.inflate(R.layout.inventory_item_edit_dialog, null)

        existingItem?.let {
            view.itemName.setText(it.name)
            view.itemDescription.setText(it.description)
            view.itemQuantity.setText(it.quantity.toString())
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (existingItem != null) null else getString(R.string.createInventoryItemTitle))
            .setView(view)
            .setPositiveButton(R.string.button_save, null)
            .setNegativeButton(R.string.button_cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onDialogSubmitted(view)
            }
        }

        return dialog
    }

    private fun onDialogSubmitted(view: View) {
        if (!checkItemValidity(view)) {
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

    private fun createInventoryItem(view: View) = InventoryItem(
        id = this.existingItem?.id ?: InventoryItemId.randomUUID(),
        name = view.itemName.text.toString().trim(),
        description = view.itemDescription.text.toString().trim(),
        quantity = view.itemQuantity.text.toString().toInt()
    )

    private fun checkEditTextValue(
        view: EditText,
        predicate: (EditText) -> Boolean,
        @StringRes message: Int
    ): Boolean {
        if (!predicate(view)) {
            val parent = view.parent.parent

            if (parent is TextInputLayout) {
                parent.error = getString(message)
            } else {
                view.error = getString(message)
            }

            return false
        }
        return true
    }

    private fun checkItemValidity(view: View): Boolean {
        val name = view.itemName

        return checkEditTextValue(name, { it.text.isNotBlank() }, R.string.error_name_blank) &&
                checkEditTextValue(
                    view.itemQuantity,
                    {
                        val value = it.text.toString().toIntOrNull()
                        value != null && value > 0
                    },
                    R.string.error_invalid_quantity
                )
    }

    private suspend fun toast(message: String) {
        withContext(Dispatchers.Main) { Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
    }
}