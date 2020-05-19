package cz.muni.fi.rpg.ui.partyList

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import dagger.android.support.DaggerDialogFragment
import kotlinx.android.synthetic.main.dialog_asssemble_party.view.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AssemblePartyDialog(
    private val userId: String,
    private val onSuccessListener: (Party) -> Unit
) : DaggerDialogFragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main

    @Inject
    lateinit var parties: PartyRepository

    private var userAttemptedToSubmit = false

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
                dialogSubmitted(view)
            }
        }

        return dialog
    }

    private fun dialogSubmitted(view: View) {
        val partyNameInput = view.partyName;
        val partyName = partyNameInput.text

        userAttemptedToSubmit = true

        showErrorIfNecessary(view)

        if (partyName.isNullOrBlank()) {
            return
        }

        val party = Party(UUID.randomUUID(), partyName.toString(), userId)

        val context = requireContext()

        launch {
            withContext(Dispatchers.IO) {
                parties.save(party)
            }

            Toast.makeText(context, "Party $partyName was created", Toast.LENGTH_LONG).show()

            dismiss()
            onSuccessListener(party)
        }
    }

    private fun showErrorIfNecessary(view: View) {
        view.partyNameLayout.error =
            if (userAttemptedToSubmit && view.partyName.text.isNullOrBlank())
                getString(R.string.assembleParty_party_name_blank) else null
    }
}