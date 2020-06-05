package cz.muni.fi.rpg.ui.partyList

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import kotlinx.android.synthetic.main.dialog_asssemble_party.view.*
import kotlinx.coroutines.*
import java.util.*

class AssemblePartyDialog(
    private val userId: String,
    private val onSuccessListener: (Party) -> Unit,
    private val parties: PartyRepository
) : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private var userAttemptedToSubmit = false

    private var pendingJob : Job? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity();

        val inflater = activity.layoutInflater;

        val view = inflater.inflate(R.layout.dialog_asssemble_party, null)

        view.partyName.addTextChangedListener { showErrorIfNecessary(view) }

        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.assembleParty_title)
            .setView(view)
            .setPositiveButton(R.string.assemblyParty_submit) { _, _ -> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialogSubmitted(dialog, view)
            }
        }

        return dialog
    }

    private fun dialogSubmitted(dialog: AlertDialog, view: View) {
        val partyNameInput = view.partyName;
        val partyName = partyNameInput.text

        userAttemptedToSubmit = true

        showErrorIfNecessary(view)

        if (partyName.isNullOrBlank()) {
            return
        }

        val party = Party(UUID.randomUUID(), partyName.toString().trim(), userId)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        view.progress.visibility = View.VISIBLE
        view.mainView.visibility = View.GONE

        pendingJob = launch {
            try {
                parties.save(party)
                toast("Party $partyName was created")
                withContext(Dispatchers.Main) { onSuccessListener(party) }
            } catch (e: CouldNotConnectToBackend) {
                Log.e(tag, e.toString())
                toast(getString(R.string.error_party_creation_no_connection))
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

    private fun showErrorIfNecessary(view: View) {
        view.partyNameLayout.error =
            if (userAttemptedToSubmit && view.partyName.text.isNullOrBlank())
                getString(R.string.assembleParty_party_name_blank) else null
    }
}