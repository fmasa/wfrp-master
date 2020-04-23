package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import java.util.*

class PartyViewModel(parties: PartyRepository, partyId: UUID): ViewModel() {
    val party = parties.getLive(partyId)
}