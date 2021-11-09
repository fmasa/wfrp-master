package cz.frantisekmasa.wfrp_master.core.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.utils.right
import kotlinx.coroutines.flow.Flow

class PartyViewModel(
    partyId: PartyId,
    parties: PartyRepository
) : ViewModel() {
    val party: Flow<Party> = parties.getLive(partyId).right()
}
