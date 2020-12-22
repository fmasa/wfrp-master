package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.Ambitions
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.model.right
import kotlinx.coroutines.flow.Flow

class CharacterMiscViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository,
    parties: PartyRepository
) : ViewModel() {

    val party: Flow<Party> = parties.getLive(characterId.partyId).right()

    val character: Flow<Character> = characters.getLive(characterId).right()

    suspend fun updateCharacterAmbitions(ambitions: Ambitions) {
        val character = characters.get(characterId)

        character.updateAmbitions(ambitions)

        characters.save(characterId.partyId, character)
    }

    suspend fun updateExperiencePoints(xpPoints: Int) {
        val character = characters.get(characterId)

        character.updatePoints(character.getPoints().copy(experience = xpPoints))

        characters.save(characterId.partyId, character)
    }
}