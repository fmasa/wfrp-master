package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.optionalParcelableArgument
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import kotlinx.android.synthetic.main.dialog_encounter.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class EncounterDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_PARTY_ID = "partyId"
        private const val EXISTING_ENCOUNTER_ID = "existingEncounterId"

        fun newInstance(partyId: UUID, existingEncounterId: EncounterId?) = EncounterDialog()
            .apply {
                arguments = bundleOf(
                    ARGUMENT_PARTY_ID to partyId,
                    EXISTING_ENCOUNTER_ID to existingEncounterId
                )
            }
    }

    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
    private val existingEncounterId: EncounterId? by optionalParcelableArgument(ARGUMENT_PARTY_ID)
    private val viewModel: EncountersViewModel by viewModel { parametersOf(partyId) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val view = activity.layoutInflater.inflate(R.layout.dialog_encounter, null)

        Form(activity).apply {
            addTextInput(view.encounterName)
                .setMaxLength(Encounter.NAME_MAX_LENGTH, false)

            addTextInput(view.encounterDescription)
                .setMaxLength(Encounter.DESCRIPTION_MAX_LENGTH, false)
        }

        val dialog = AlertDialog.Builder(activity)
            .setTitle(
                if (existingEncounterId == null)
                    getString(R.string.title_encounter_create)
                else null
            )
            .setView(view)
            .setPositiveButton(R.string.button_save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                it.isEnabled = false
                dialogSubmitted(view)
            }
        }

        return dialog
    }

    private fun dialogSubmitted(view: View) {
        launch {
            viewModel.createEncounter(
                view.encounterName.getValue(),
                view.encounterDescription.getValue()
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    R.string.message_encounter_saved,
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }
    }
}