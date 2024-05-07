package cz.frantisekmasa.wfrp_master.common.core

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow

class PartyScreenModel(
    partyId: PartyId,
    parties: PartyRepository,
) : ScreenModel {
    val party: Flow<Party> = parties.getLive(partyId).right()
}
