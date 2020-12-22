package cz.muni.fi.rpg.ui.character.inventory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.character.NotEnoughMoney
import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.muni.fi.rpg.ui.common.adapters.SpinnerAdapterWithWidthMatchingSelectedItem
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TransactionDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        const val ARGUMENT_CHARACTER_ID = "characterId"

        fun newInstance(characterId: CharacterId) = TransactionDialog().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    private val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val layoutInflater = activity.layoutInflater

        val view = layoutInflater.inflate(R.layout.dialog_transaction, null)

        view.findViewById<Spinner>(R.id.directionSpinner).adapter = SpinnerAdapterWithWidthMatchingSelectedItem(
            ArrayAdapter(
                requireContext(),
                R.layout.title_spinner_dropdown_item,
                listOf(getString(R.string.money_subtract), getString(R.string.money_add))
            ),
            R.layout.title_spinner_item,
            layoutInflater
        )

        val dialog = AlertDialog.Builder(activity, R.style.FormDialog)
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
        val notEnoughMoneyError = view.findViewById<View>(R.id.notEnoughMoneyError)
        notEnoughMoneyError.visibility = View.GONE

        val amount = Money.crowns(getIntValue(view.findViewById(R.id.crownsInput))) +
                Money.shillings(getIntValue(view.findViewById(R.id.shillingsInput))) +
                Money.pennies(getIntValue(view.findViewById(R.id.penniesInput)))

        launch {
            try {
                if (view.findViewById<Spinner>(R.id.directionSpinner).selectedItemId == 0L) {
                    viewModel.subtractMoney(amount)
                } else {
                    viewModel.addMoney(amount)
                }

                dismiss()
            } catch (e: NotEnoughMoney) {
                withContext(Dispatchers.Main) { notEnoughMoneyError.visibility = View.VISIBLE }
            }
        }
    }

    private fun getIntValue(view: TextInput) = view.getValue().toIntOrNull() ?: 0
}