package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import kotlinx.coroutines.flow.Flow

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
}