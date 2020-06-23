package cz.muni.fi.rpg.ui.gameMaster

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.fasterxml.jackson.databind.json.JsonMapper
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.ui.common.parcelableArgument
import kotlinx.android.synthetic.main.dialog_invitation.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import timber.log.Timber

class InvitationDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        const val ARGUMENT_INVITATION = "invitation"

        fun newInstance(invitation: Invitation) = InvitationDialog().apply {
            arguments = bundleOf(ARGUMENT_INVITATION to invitation)
        }
    }

    private val invitation: Invitation by parcelableArgument(ARGUMENT_INVITATION)
    private val jsonMapper: JsonMapper by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_invitation, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        val codeGenerating = launch {
            val jsonInvitation = withContext(Dispatchers.IO) {
                jsonMapper.writeValueAsString(invitation)
            }

            launch(Dispatchers.Main) { view.partyInviteQrCode.drawCode(jsonInvitation) }

            initializeButton(view, invitation, jsonInvitation)
        }

        dialog.setOnDismissListener { codeGenerating.cancel() }

        return dialog
    }

    private suspend fun initializeButton(
        view: View,
        invitation: Invitation,
        jsonInvitation: String
    ) {
        val link = Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://dnd-master-58fca.web.app/app/invitation?invitation=$jsonInvitation")
            androidParameters {  }
            domainUriPrefix = "https://wfrp.page.link"
        }.await()

        Timber.d("Invitation link was generated: ${link.shortLink}")

        withContext(Dispatchers.Main) {
            view.shareButton.setOnClickListener {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Join ${invitation.partyName} using this link: ${link.shortLink}"
                    )
                    type = "text/plain"
                }

                startActivity(Intent.createChooser(sendIntent, "Send link to your friends"))
            }
            view.shareButton.isEnabled = true
        }
    }
}