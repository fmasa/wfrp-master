package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.character.Points
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterStatsViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    val character: LiveData<Either<CharacterNotFound, Character>> = characters.getLive(characterId)

    fun incrementWounds() = updatePoints { it.copy(wounds = it.wounds + 1) }
    fun decrementWounds() = updatePoints { it.copy(wounds = it.wounds - 1) }
    fun incrementFortunePoints() = updatePoints { it.copy(fortune = it.fortune + 1) }
    fun decrementFortunePoints() = updatePoints { it.copy(fortune = it.fortune - 1) }

    fun incrementFatePoints() = addFatePoints(1)
    fun decrementFatePoints() = addFatePoints(-1)

    fun incrementInsanityPoints() = updatePoints { it.copy(insanity = it.insanity + 1) }
    fun decrementInsanityPoints() = updatePoints { it.copy(insanity = it.insanity - 1) }

    private fun addFatePoints(addition: Int) = updatePoints { it.updateFate(it.fate + addition) }

    private fun updatePoints(mutation: (points: Points) -> Points) {
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