package cz.muni.fi.rpg.ui.gameMaster

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.ui.common.stringArgument
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.android.synthetic.main.dialog_party_rename.view.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*

class RenamePartyDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    companion object {
        fun newInstance(partyId: UUID, currentName: String) = RenamePartyDialog().apply {
            arguments = bundleOf(
                "partyId" to partyId,
                "currentName" to currentName
            )
        }
    }

    private val partyId: UUID by serializableArgument("partyId")
    private val currentName: String by stringArgument("currentName", "")

    private val viewModel: GameMasterViewModel by viewModel { parametersOf(partyId) }

    private var pendingJob: Job? = null

    private lateinit var form: Form

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()

        val inflater = activity.layoutInflater

        val view = inflater.inflate(R.layout.dialog_party_rename, null)

        form = Form(requireContext()).apply {
            addTextInput(view.partyNameInput).apply {
                setMaxLength(Party.NAME_MAX_LENGTH)
                setDefaultValue(currentName)
                addLiveRule(R.string.error_party_name_blank) { !it.isNullOrBlank() }
            }
        }

        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.title_party_rename)
            .setView(view)
            .setPositiveButton(R.string.button_save) { _, _ -> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialogSubmitted(dialog, view)
            }
        }

        return dialog
    }

    private fun dialogSubmitted(dialog: AlertDialog, view: View) {
        if (!form.validate()) {
            return
        }

        val partyName = view.partyNameInput.getValue()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        view.progress.visibility = View.VISIBLE
        view.partyNameInput.visibility = View.GONE

        Timber.d("Dialog submitted, trying to update party name")

        pendingJob = launch {
            try {
                viewModel.renameParty(partyName)
                toast(getString(R.string.message_party_updated))
                Timber.d(tag, "Party was renamed")
            } catch (e: CouldNotConnectToBackend) {
                Timber.i(e, "User could not rename party, because (s)he is offline")
                toast(getString(R.string.error_party_update_no_connection))
            } catch (e: Throwable) {
                toast(getString(R.string.error_unkown))
                Timber.e(e)
            } finally {
                withContext(Dispatchers.Main) { dismiss() }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        pendingJob?.cancel()
    }

    private suspend fun toast(message: String) = withContext(Dispatchers.Main) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}