package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId

class CharacterStatsViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository,
) : ViewModel() {
    suspend fun updatePoints(points: Points) {
        val character = characters.get(characterId)

        characters.save(characterId.partyId, character.updatePoints(points))
    }

    suspend fun updateCharacterAmbitions(ambitions: Ambitions) {
        val character = characters.get(characterId)

        characters.save(characterId.partyId, character.updateAmbitions(ambitions))
    }
}
