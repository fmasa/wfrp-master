package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

class PartyListViewModel(
    private val parties: PartyRepository
): ViewModel() {

    fun liveForUser(userId: String): Flow<List<Party>> {
        return parties.forUserLive(userId)
    }

    suspend fun archive(partyId: UUID) {
        val party = parties.get(partyId)

        party.archive()

        parties.save(party)
    }
}