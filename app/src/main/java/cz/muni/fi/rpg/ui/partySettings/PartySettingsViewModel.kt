package cz.muni.fi.rpg.ui.partySettings

import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.utils.right
import kotlinx.coroutines.flow.Flow
import java.util.*

class PartySettingsViewModel(
    private val partyId: UUID,
    private val parties: PartyRepository
) {

    val party: Flow<Party> = parties.getLive(partyId).right()
}