package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.model.right

class CharacterMiscViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository,
    private val parties: PartyRepository
) : ViewModel() {

    val party: LiveData<Party> = parties.getLive(characterId.partyId).right()

    val character: LiveData<Character> = characters.getLive(characterId).right()

    suspend fun updateCharacterAmbitions(ambitions: Ambitions) {
        val character = characters.get(characterId)

        character.updateAmbitions(ambitions)

        characters.save(characterId.partyId, character)
    }
}