package cz.muni.fi.rpg.viewModels

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientViewModelStoreOwner
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.json.JsonMapper
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class JoinPartyViewModel(
    private val invitationProcessor: InvitationProcessor,
    private val jsonMapper: JsonMapper,
) : ViewModel() {

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

@Composable
fun provideJoinPartyViewModel(): JoinPartyViewModel =
    AmbientViewModelStoreOwner.current.getViewModel()