package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import java.util.*

class PartyViewModel(
    partyId: UUID,
    parties: PartyRepository
) : ViewModel() {
    val party = parties.getLive(partyId)
}