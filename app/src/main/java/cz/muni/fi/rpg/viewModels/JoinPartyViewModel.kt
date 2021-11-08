package cz.muni.fi.rpg.viewModels

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.muni.fi.rpg.model.domain.invitation.InvitationProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class JoinPartyViewModel(
    private val invitationProcessor: InvitationProcessor,
    private val serializer: Json,
    private val parties: PartyRepository,
) : ViewModel() {

    suspend fun acceptInvitation(userId: String, invitation: Invitation) {
        invitationProcessor.accept(userId, invitation)
    }

    fun userParties(userId: String): Flow<List<Party>> {
        return parties.forUserLive(userId)
    }

    suspend fun deserializeInvitationJson(json: String): Invitation? = withContext(Dispatchers.IO) {
        @Suppress("BlockingMethodInNonBlockingContext")
        try {
            serializer.decodeFromString<Invitation>(json)
        } catch (e: Throwable) {
            Timber.w(e)

            null
        }
    }
}

@Composable
fun provideJoinPartyViewModel(): JoinPartyViewModel =
    LocalViewModelStoreOwner.current!!.getViewModel()
