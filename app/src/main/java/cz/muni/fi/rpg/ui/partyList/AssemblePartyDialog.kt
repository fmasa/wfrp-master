package cz.muni.fi.rpg.ui.partyList

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class AssemblePartyDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    private val parties: PartyRepository by inject()
    private val auth: AuthenticationViewModel by viewModel()
    private var pendingJob: Job? = null

    private var listener: (Party) -> Unit = {}

    private lateinit var form: Form

    fun setOnSuccessListener(listener: (Party) -> Unit): AssemblePartyDialog {
        this.listener = listener

        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()

        val inflater = activity.layoutInflater

        val view = inflater.inflate(R.layout.dialog_asssemble_party, null)

        form = Form(requireContext()).apply {
            addTextInput(view.findViewById<TextInput>(R.id.partyNameInput)).apply {
                setMaxLength(Party.NAME_MAX_LENGTH)
                addLiveRule(R.string.error_party_name_blank) {
                    !it.isNullOrBlank()
                }
            }
        }

        val dialog = AlertDialog.Builder(activity, R.style.FormDialog)
            .setTitle(R.string.assembleParty_title)
            .setView(view)
            .setPositiveButton(R.string.button_create) { _, _ -> }
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

        val partyId = UUID.randomUUID()
        val partyName = view.findViewById<TextInput>(R.id.partyNameInput).getValue()

        val userId = auth.getUserId()
        val party = Party(
            id = partyId,
            name = partyName,
            gameMasterId = userId,
            users = setOf(userId)
        )

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        view.findViewById<View>(R.id.progress).visibility = View.VISIBLE
        view.findViewById<View>(R.id.mainView).visibility = View.GONE

        Timber.d("Dialog submitted, trying to assemble new party")

        pendingJob = launch {
            try {
                parties.save(party)
                longToast("Party $partyName was created")
                Timber.d(tag, "Party $partyName was successfully created")

                Firebase.analytics.logEvent("create_party") {
                    param("id", party.id.toString())
                }

                withContext(Dispatchers.Main) { listener(party) }
            } catch (e: CouldNotConnectToBackend) {
                Timber.i(e, "User could not assemble party, because (s)he is offline")
                longToast(getString(R.string.error_party_creation_no_connection))
            } catch (e: Throwable) {
                longToast(getString(R.string.error_unkown))
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

    private suspend fun longToast(message: String) = withContext(Dispatchers.Main) {
        toast(message, Toast.LENGTH_LONG)
    }
}