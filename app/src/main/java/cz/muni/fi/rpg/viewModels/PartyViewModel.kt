package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import java.util.*

class PartyViewModel(
    private val parties: PartyRepository,
    private val partyId: UUID
) : ViewModel() {
    val party = parties.getLive(partyId)

    suspend fun updatePartyAmbitions(ambitions: Ambitions) {
        val party = parties.get(partyId)

        party.updateAmbitions(ambitions)

        parties.save(party)
    }
}