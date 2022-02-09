package cz.muni.fi.rpg.ui.partySettings

import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow

class PartySettingsViewModel(
    private val partyId: PartyId,
    private val parties: PartyRepository
) : ViewModel() {
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
