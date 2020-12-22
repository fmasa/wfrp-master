package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.character.Points
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CharacterStatsViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    val character: Flow<Either<CharacterNotFound, Character>> = characters.getLive(characterId)

    fun updatePoints(mutation: (points: Points) -> Points) {
        launch {
            val character = characters.get(characterId)
            try {
                character.updatePoints(mutation(character.getPoints()))
                characters.save(characterId.partyId, character)
            } catch (e: IllegalArgumentException) {
            }
        }
    }
}