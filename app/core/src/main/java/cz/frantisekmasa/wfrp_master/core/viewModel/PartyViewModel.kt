package cz.frantisekmasa.wfrp_master.core.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.utils.right

class PartyViewModel(
    partyId: PartyId,
    parties: PartyRepository
) : ViewModel() {
    val party: LiveData<Party> = parties.getLive(partyId).right().asLiveData()
}
