package cz.muni.fi.rpg.ui.gameMaster

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.fasterxml.jackson.databind.json.JsonMapper
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Invitation
import kotlinx.android.synthetic.main.dialog_invitation.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvitationDialog(
    private val invitation: Invitation,
    private val jsonMapper: JsonMapper
) : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_invitation, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        val codeGenerating = launch {
            view.partyInviteQrCode.drawCode(
                withContext(Dispatchers.IO) { jsonMapper.writeValueAsString(invitation) }
            )
        }

        dialog.setOnDismissListener { codeGenerating.cancel() }

        return dialog
    }

}