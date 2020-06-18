package cz.muni.fi.rpg.ui.joinParty

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.json.JsonMapper
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.ui.MainActivity
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class InvitationLinkActivity : AppCompatActivity(R.layout.activity_invitation_link),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        const val tag = "InvitationLinkActivity"
    }

    private val auth: AuthenticationViewModel by inject()
    private val jsonMapper: JsonMapper by inject()
    private val invitationProcessor: InvitationProcessor by inject()

    override fun onStart() {
        super.onStart()

        launch {
            authenticate()
            acceptDynamicLinks()
        }
    }

    private suspend fun authenticate(): Boolean {
        return auth.isAuthenticated() || auth.authenticateAnonymously()
    }

    private suspend fun acceptDynamicLinks() {
        try {
            val link = Firebase.dynamicLinks.getDynamicLink(intent).await()?.link ?: return
            val invitationJson = link.getQueryParameter("invitation")

            if (invitationJson == null) {
                Log.d(tag, "Dynamic link URI does not have 'invitation' query parameter")

                withContext(Dispatchers.Main) { openPartyList() }
                return
            }

            @Suppress("BlockingMethodInNonBlockingContext")
            val invitation = jsonMapper.readValue(invitationJson, Invitation::class.java)

            withContext(Dispatchers.Main) {
                JoinPartyDialog(auth.getUserId(), invitation, invitationProcessor)
                    .setOnErrorListener { openPartyList() }
                    .setOnSuccessListener { openPartyList() }
                    .setOnDismissListener { openPartyList() }
                    .show(supportFragmentManager, "JoinPartyDialog")
            }
        } catch (e: Throwable) {
            Log.w("MainActivity", "Could not process Dynamic Link data", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Invalid link", Toast.LENGTH_SHORT).show()
                openPartyList()
            }
        }
    }

    private fun openPartyList() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}