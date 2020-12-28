package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.json.JsonMapper
import cz.muni.fi.rpg.model.domain.invitation.AlreadyInParty
import cz.muni.fi.rpg.model.domain.invitation.InvalidInvitation
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class InvitationScannerViewModel(
    private val invitationProcessor: InvitationProcessor,
    private val jsonMapper: JsonMapper,
) : ViewModel() {
    /**
     * @throws InvalidInvitation
     * @throws AlreadyInParty
     */
    suspend fun acceptInvitation(userId: String, invitation: Invitation) {
        invitationProcessor.accept(userId, invitation)
    }

    suspend fun deserializeInvitationJson(json: String): Invitation? = withContext(Dispatchers.IO) {
        @Suppress("BlockingMethodInNonBlockingContext")
        try {
            jsonMapper.readValue(json, Invitation::class.java)
        } catch (e: Throwable) {
            Timber.w(e)

            null
        }
    }
}