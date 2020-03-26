package cz.muni.fi.rpg.partyList

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.Party
import cz.muni.fi.rpg.model.firestore.FirestorePartyRepository
import java.util.*

class AssemblePartyDialog(private val userId: String) : DialogFragment() {

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

        val partyNameInput = requireDialog().findViewById<TextInputEditText>(R.id.partyName)

        val party = Party(UUID.randomUUID(), partyNameInput.text.toString(), userId);



        FirestorePartyRepository().save(party).run {
            Toast.makeText(
                requireContext(),
                "Party ${partyNameInput.text.toString()} was created", Toast.LENGTH_LONG
            ).show()

            dismiss()
        };

    }
}