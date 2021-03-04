package cz.muni.fi.rpg.viewModels

import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.fasterxml.jackson.databind.json.JsonMapper
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class JoinPartyViewModel(
    private val invitationProcessor: InvitationProcessor,
    private val jsonMapper: JsonMapper,
    private val parties: PartyRepository,
) : ViewModel() {

    suspend fun acceptInvitation(userId: String, invitation: Invitation) {
        invitationProcessor.accept(userId, invitation)
    }

    fun userParties(userId: String): LiveData<List<Party>> {
        return parties.forUserLive(userId).asLiveData()
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
    LocalViewModelStoreOwner.current.getViewModel()