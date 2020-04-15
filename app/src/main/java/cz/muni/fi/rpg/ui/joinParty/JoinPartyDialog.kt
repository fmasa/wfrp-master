package cz.muni.fi.rpg.ui.joinParty

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.bold
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.invitation.InvalidInvitation
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.Invitation
import kotlinx.android.synthetic.main.dialog_join_party.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

typealias Listener = () -> Unit

class JoinPartyDialog(
    private val userId: String,
    private val invitation: Invitation,
    private val invitationProcessor: InvitationProcessor
) : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private var onSuccessListener: Listener = {}
    private var onErrorListener: Listener = {}
    private var onDismissListener: Listener = {}

    fun setOnSuccessListener(listener: Listener): JoinPartyDialog {
        onSuccessListener = listener

        return this
    }

    fun setOnErrorListener(listener: Listener): JoinPartyDialog {
        onErrorListener = listener

        return this
    }

    fun setOnDismissListener(listener: Listener): JoinPartyDialog {
        onDismissListener = listener

        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isCancelable = false
    }

    private fun dismissAndNotify() {
        dismiss()
        onDismissListener()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity();
        val view = activity.layoutInflater.inflate(R.layout.dialog_join_party, null)

        view.partyNamePrompt.text = SpannableStringBuilder()
            .append(getString(R.string.join_party_dialog_party_name))
            .append(" ")
            .bold { append(invitation.partyName) }
            .append(".")

        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.join_party_dialog_title)
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(R.string.button_continue, null)
            .setNegativeButton(R.string.button_cancel) { _, _ -> dismissAndNotify() }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onConfirm(dialog, view)
            }
        }

        return dialog
    }

    private fun onConfirm(dialog: AlertDialog, view: View) {
        view.partyNamePrompt.visibility = View.GONE
        view.prompt.visibility = View.GONE
        view.progress.visibility = View.VISIBLE

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false

        launch {
            try {
                invitationProcessor.accept(userId, invitation)
                onSuccessListener()
            } catch (e: InvalidInvitation) {
                val error = "Invitation token is not valid"
                Log.e(tag, error, e)
                Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show()

                dismissAndNotify()
            }
        }
    }

}