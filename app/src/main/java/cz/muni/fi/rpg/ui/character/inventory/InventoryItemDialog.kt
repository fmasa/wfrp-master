package cz.muni.fi.rpg.ui.character.inventory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemId
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.optionalParcelableArgument
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.android.synthetic.main.inventory_item_edit_dialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class InventoryItemDialog : DialogFragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
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

    private val existingItem: InventoryItem? by optionalParcelableArgument(ARGUMENT_ITEM)
    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)

    private val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val view = activity.layoutInflater.inflate(R.layout.inventory_item_edit_dialog, null)

        val dialog = AlertDialog.Builder(requireContext(), R.style.FormDialog)
            .setTitle(if (existingItem != null) null else getString(R.string.createInventoryItemTitle))
            .setView(view)
            .setPositiveButton(R.string.button_save, null)
            .setNegativeButton(R.string.button_cancel, null)
            .create()


        val form = Form(requireContext()).apply {
            addTextInput(view.nameInput).apply {
                setMaxLength(InventoryItem.NAME_MAX_LENGTH, false)
                setNotBlank(getString(R.string.error_name_blank))
                existingItem?.let { setDefaultValue(it.name) }
            }

            addTextInput(view.descriptionInput).apply {
                setMaxLength(InventoryItem.DESCRIPTION_MAX_LENGTH, false)
                existingItem?.let { setDefaultValue(it.description) }
            }

            addTextInput(view.quantityInput).apply {
                setDefaultValue(existingItem?.quantity?.toString() ?: "1")
                addLiveRule(getString(R.string.error_invalid_quantity)) {
                    val value = it.toString().toIntOrNull()

                    value != null && value > 0
                }
            }
        }

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onDialogSubmitted(form, view)
            }
        }

        return dialog
    }

    private fun onDialogSubmitted(form: Form, view: View) {
        if (!form.validate()) {
            return
        }

        val inventoryItem = createInventoryItem(view)

        launch {
            try {
                viewModel.saveInventoryItem(inventoryItem)

                toast(getString(R.string.inventory_toast_item_saved))
                dismiss()
            } catch (e: Throwable) {
                toast("Item couldn't be added to your inventory.")
                Timber.e(e)
            }
        }
    }

    private fun createInventoryItem(view: View) = InventoryItem(
        id = this.existingItem?.id ?: InventoryItemId.randomUUID(),
        name = view.nameInput.getValue(),
        description = view.descriptionInput.getValue(),
        quantity = view.quantityInput.getValue().toInt()
    )

    private suspend fun toast(message: String) {
        withContext(Dispatchers.Main) { Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
    }
}