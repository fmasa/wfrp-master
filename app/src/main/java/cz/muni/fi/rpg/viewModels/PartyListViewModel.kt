package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class PartyListViewModel(
    private val parties: PartyRepository
): ViewModel() {

    fun liveForUser(userId: String): Flow<List<Party>> {
        return parties.forUserLive(userId)
    }

    suspend fun archive(partyId: PartyId) {
        val party = parties.get(partyId)

        party.archive()

        parties.save(party)
    }

    /**
     * @throws CouldNotConnectToBackend
     */
    suspend fun createParty(partyName: String, gameMasterId: String): PartyId {
        val partyId = PartyId.generate()
        val party = Party(
            id = partyId,
            name = partyName,
            gameMasterId = gameMasterId,
            users = setOf(gameMasterId)
        )

        parties.save(party)

        Timber.d("Party $partyName was successfully created")
        Firebase.analytics.logEvent("create_party") {
            param("id", party.id.toString())
        }

        return partyId
    }
}