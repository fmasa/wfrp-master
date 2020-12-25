package cz.muni.fi.rpg.ui.joinParty

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.text.bold
import androidx.fragment.app.DialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.invitation.AlreadyInParty
import cz.muni.fi.rpg.model.domain.invitation.InvalidInvitation
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.common.stringArgument
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.viewModels.JoinPartyViewModel
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class JoinPartyDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    companion object {
        fun newInstance(
            userId: String,
            invitation: Invitation
        ) = JoinPartyDialog().apply{
            arguments = bundleOf(
                "userId" to userId,
                "invitation" to invitation
            )
        }
    }
    interface Listener {
        fun onSuccessfulPartyJoin()
        fun onDialogDismiss()
    }

    private val viewModel: JoinPartyViewModel by viewModel()

    private val userId by stringArgument("userId")
    private val invitation: Invitation by parcelableArgument("invitation")

    private lateinit var listener: Listener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        check(context is Listener) { "$context must implement JoinPartyDialog.Listener" }
        listener = context
    }

    private var joining: Job? = null

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        joining?.cancel()
        dismissAndNotify()
    }

    private fun dismissAndNotify() {
        dismiss()
        listener.onDialogDismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val view = activity.layoutInflater.inflate(R.layout.dialog_join_party, null)

        view.findViewById<TextView>(R.id.partyNamePrompt).text = SpannableStringBuilder()
            .append(getString(R.string.join_party_dialog_party_name))
            .append(" ")
            .bold { append(invitation.partyName) }
            .append(".")

        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.title_joinParty)
            .setView(view)
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
        view.findViewById<View>(R.id.partyNamePrompt).visibility = View.GONE
        view.findViewById<View>(R.id.prompt).visibility = View.GONE
        view.findViewById<View>(R.id.progress).visibility = View.VISIBLE

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false

        joining = launch {
            try {
                viewModel.acceptInvitation(userId, invitation)

                Firebase.analytics.logEvent(FirebaseAnalytics.Event.JOIN_GROUP) {
                    param(FirebaseAnalytics.Param.GROUP_ID, invitation.partyId.toString())
                }

                listener.onSuccessfulPartyJoin()

                return@launch
            } catch (e: InvalidInvitation) {
                invitationError(getString(R.string.error_invalid_invitation), e)
            } catch (e: AlreadyInParty) {
                invitationError(getString(R.string.error_already_party_member), e)
            } catch (e: CancellationException) {
            } catch (e: Throwable) {
                Timber.e(e, getString(R.string.error_unkown))
                invitationError(getString(R.string.error_unkown), e)
            }

            dismissAndNotify()
        }
    }

    private fun invitationError(message: String, e: Throwable) {
        toast(message)
        Timber.i(e)
    }
}