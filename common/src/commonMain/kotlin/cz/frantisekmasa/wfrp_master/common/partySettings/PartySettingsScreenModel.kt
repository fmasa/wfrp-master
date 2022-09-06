package cz.frantisekmasa.wfrp_master.common.partySettings

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow

class PartySettingsScreenModel(
    private val partyId: PartyId,
    private val parties: PartyRepository
) : ScreenModel {
    val party: Flow<Party> = parties.getLive(partyId).right()

    suspend fun updateSettings(change: (Settings) -> Settings) {
        parties.update(partyId) {
            it.updateSettings(change(it.settings))
        }
    }

    suspend fun renameParty(newName: String) {
        parties.update(partyId) { it.rename(newName) }
    }
}
