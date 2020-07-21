package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import java.util.*

class PartyListViewModel(
    private val parties: PartyRepository
): ViewModel() {

    fun liveForUser(userId: String): LiveData<List<Party>> {
        return parties.forUser(userId)
    }

    suspend fun archive(partyId: UUID) {
        val party = parties.get(partyId)

        party.archive()

        parties.save(party)
    }
}