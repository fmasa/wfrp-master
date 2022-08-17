package cz.frantisekmasa.wfrp_master.common.character.characteristics

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId

class CharacteristicsScreenModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository,
) : ScreenModel {
    suspend fun updatePoints(points: Points) {
        val character = characters.get(characterId)

        characters.save(characterId.partyId, character.updatePoints(points))
    }
}