package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.core.utils.right

class CharacterMiscViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository,
    parties: PartyRepository
) : ViewModel() {

    val party: LiveData<Party> = parties.getLive(characterId.partyId).right().asLiveData()

    suspend fun updateCharacterAmbitions(ambitions: Ambitions) {
        val character = characters.get(characterId)

        character.updateAmbitions(ambitions)

        characters.save(characterId.partyId, character)
    }

    suspend fun updatePoints(points: Points) {
        val character = characters.get(characterId)

        character.updatePoints(points)

        characters.save(characterId.partyId, character)
    }
}