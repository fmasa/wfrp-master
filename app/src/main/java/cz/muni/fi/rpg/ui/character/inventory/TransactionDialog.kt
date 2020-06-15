package cz.muni.fi.rpg.ui.character.inventory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.NotEnoughMoney
import cz.muni.fi.rpg.model.domain.common.Money
import cz.muni.fi.rpg.ui.common.adapters.SpinnerAdapterWithWidthMatchingSelectedItem
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.android.synthetic.main.dialog_transaction.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionDialog(
    private val viewModel: InventoryViewModel
) : DialogFragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val layoutInflater = activity.layoutInflater

        val view = layoutInflater.inflate(R.layout.dialog_transaction, null)

        view.directionSpinner.adapter = SpinnerAdapterWithWidthMatchingSelectedItem(
            ArrayAdapter(
                requireContext(),
                R.layout.title_spinner_dropdown_item,
                listOf(getString(R.string.money_subtract), getString(R.string.money_add))
            ),
            R.layout.title_spinner_item,
            layoutInflater
        )

        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .setPositiveButton(R.string.button_submit, null).create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onConfirm(view)
            }
        }

        return dialog
    }

    private fun onConfirm(view: View) {
        view.notEnoughMoneyError.visibility = View.GONE

        val amount = Money.crowns(getIntValue(view.crownsInput)) +
                Money.shillings(getIntValue(view.shillingsInput)) +
                Money.pennies(getIntValue(view.penniesInput))

        launch {
            try {
                if (view.directionSpinner.selectedItemId == 0L) {
                    viewModel.subtractMoney(amount)
                } else {
                    viewModel.addMoney(amount)
                }

                dismiss()
            } catch (e: NotEnoughMoney) {
                withContext(Dispatchers.Main) { view.notEnoughMoneyError.visibility = View.VISIBLE }
            }
        }
    }

    private fun getIntValue(view: TextInput) = view.getValue().toIntOrNull() ?: 0
}