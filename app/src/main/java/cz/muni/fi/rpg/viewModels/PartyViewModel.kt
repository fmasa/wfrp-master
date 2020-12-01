package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.muni.fi.rpg.model.right
import kotlinx.coroutines.flow.Flow
import java.util.*

class PartyViewModel(
    partyId: UUID,
    parties: PartyRepository
) : ViewModel() {
    val party: Flow<Party> = parties.getLive(partyId).right()
}