package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.optionalParcelableArgument
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
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
        private const val ARGUMENT_DEFAULTS = "existingEncounterId"

        internal fun newInstance(partyId: UUID, defaults: Defaults?) = EncounterDialog()
            .apply {
                arguments = bundleOf(
                    ARGUMENT_PARTY_ID to partyId,
                    ARGUMENT_DEFAULTS to defaults
                )
            }
    }

    @Parcelize
    internal data class Defaults(
        @TypeParceler<UUID, UUIDParceler>
        val id: UUID,
        val name: String,
        val description: String
    ) : Parcelable

    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
    private val defaults: Defaults? by optionalParcelableArgument(ARGUMENT_DEFAULTS)
    private val viewModel: EncountersViewModel by viewModel { parametersOf(partyId) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val view = activity.layoutInflater.inflate(R.layout.dialog_encounter, null)

        val form = Form(activity).apply {
            addTextInput(view.encounterName).apply {
                addLiveRule(R.string.error_cannot_be_empty) { ! it.isNullOrBlank() }
                setMaxLength(Encounter.NAME_MAX_LENGTH, false)
                defaults?.let { setDefaultValue(it.name) }
            }

            addTextInput(view.encounterDescription).apply {
                setMaxLength(Encounter.DESCRIPTION_MAX_LENGTH, false)
                defaults?.let { setDefaultValue(it.description) }
            }
        }

        val dialog = AlertDialog.Builder(activity)
            .setTitle(
                if (defaults == null)
                    getString(R.string.title_encounter_create)
                else null
            )
            .setView(view)
            .setPositiveButton(R.string.button_save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (! form.validate()) {
                    return@setOnClickListener
                }

                it.isEnabled = false
                view.mainView.toggleVisibility(false)
                view.progress.toggleVisibility(true)

                dialogSubmitted(view)
            }
        }

        return dialog
    }

    private fun dialogSubmitted(view: View) {
        launch {
            val defaults = defaults
            if (defaults != null) {
                viewModel.updateEncounter(
                    defaults.id,
                    view.encounterName.getValue(),
                    view.encounterDescription.getValue()
                )
            } else {
                viewModel.createEncounter(
                    view.encounterName.getValue(),
                    view.encounterDescription.getValue()
                )
            }
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