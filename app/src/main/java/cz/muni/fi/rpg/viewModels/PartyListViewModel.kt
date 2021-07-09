package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.core.auth.UserId
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import timber.log.Timber

class PartyListViewModel(
    private val parties: PartyRepository
) : ViewModel() {

    fun liveForUser(userId: String): LiveData<List<Party>> {
        return parties.forUserLive(userId).asLiveData()
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

    suspend fun leaveParty(partyId: PartyId, userId: UserId) {
        val party = parties.get(partyId)

        party.leave(userId)

        parties.save(party)
    }
}
