package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import arrow.core.Either
import cz.frantisekmasa.wfrp_master.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Points

class CharacterStatsViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository,
) : ViewModel() {
    suspend fun updatePoints(points: Points) {
        val character = characters.get(characterId)

        character.updatePoints(points)
        characters.save(characterId.partyId, character)
    }

    suspend fun updateCharacterAmbitions(ambitions: Ambitions) {
        val character = characters.get(characterId)

        character.updateAmbitions(ambitions)

        characters.save(characterId.partyId, character)
    }
}