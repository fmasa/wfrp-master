package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.core.utils.right
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

    suspend fun updatePoints(points: Points) {
        val character = characters.get(characterId)

        character.updatePoints(points)

        characters.save(characterId.partyId, character)
    }
}