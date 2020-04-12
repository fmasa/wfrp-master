package cz.muni.fi.rpg.partyList

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.Party
import cz.muni.fi.rpg.model.PartyRepository
import dagger.android.support.DaggerDialogFragment
import kotlinx.android.synthetic.main.dialog_asssemble_party.*
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity();

        val inflater = activity.layoutInflater;

        return AlertDialog.Builder(activity)
            .setTitle(R.string.assembleParty_title)
            .setView(inflater.inflate(R.layout.dialog_asssemble_party, null))
            .setPositiveButton(R.string.assemblyParty_submit) { _, _ -> dialogSubmitted() }
            .create()
    }

    private fun dialogSubmitted() {
        val partyNameInput = requireDialog().partyName

        val party = Party(UUID.randomUUID(), partyNameInput.text.toString(), userId);

        val context = requireContext()

        launch {
            withContext(Dispatchers.IO) {
                parties.save(party)
            }

            Toast.makeText(
                context,
                "Party ${partyNameInput.text.toString()} was created",
                Toast.LENGTH_LONG
            ).show()

            dismiss()
            onSuccessListener(party)
        }
    }
}