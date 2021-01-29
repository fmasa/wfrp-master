package cz.muni.fi.rpg.ui.partySettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.domain.party.settings.Settings
import cz.frantisekmasa.wfrp_master.core.utils.right

class PartySettingsViewModel(
    private val partyId: PartyId,
    private val parties: PartyRepository
) : ViewModel() {
    val party: LiveData<Party> = parties.getLive(partyId).right().asLiveData()

    suspend fun updateSettings(change: (Settings) -> Settings) {
        val party = parties.get(partyId)

        party.updateSettings(change(party.getSettings()))

        parties.save(party)
    }

    suspend fun renameParty(newName: String) {
        val party = parties.get(partyId)

        party.rename(newName)

        parties.save(party)
    }
}