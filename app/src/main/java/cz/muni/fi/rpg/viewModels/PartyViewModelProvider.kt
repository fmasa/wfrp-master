package cz.muni.fi.rpg.viewModels

import cz.muni.fi.rpg.model.domain.party.PartyRepository
import java.util.UUID
import javax.inject.Inject

class PartyViewModelProvider @Inject constructor(private val parties: PartyRepository) {
    fun provide(partyId: UUID) = FixedViewModelFactory(PartyViewModel(parties, partyId))
}