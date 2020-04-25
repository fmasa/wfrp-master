package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.character.Points
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.min

class CharacterViewModel(
    private val characters: CharacterRepository,
    private val partyId: UUID,
    private val userId: String
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    val character = characters.getLive(partyId, userId)

    fun incrementWounds() = updatePoints { it.copy(wounds = it.wounds + 1) }
    fun decrementWounds() = updatePoints { it.copy(wounds = it.wounds - 1) }

    fun incrementFortunePoints() = updatePoints { it.copy(fortune = it.fortune + 1) }
    fun decrementFortunePoints() = updatePoints { it.copy(fortune = it.fortune - 1) }

    fun incrementFatePoints() = addFatePoints(1)
    fun decrementFatePoints() = addFatePoints(-1)

    fun incrementInsanityPoints() = updatePoints { it.copy(insanity = it.insanity + 1) }
    fun decrementInsanityPoints() = updatePoints { it.copy(insanity = it.insanity - 1) }

    private fun addFatePoints(addition: Int) = updatePoints {
        val newFatePoints = it.fate + addition

        it.copy(fate = newFatePoints, fortune = min(it.fortune, newFatePoints))
    }

    private fun updatePoints(mutation: (points: Points) -> Points) {
        launch {
            val character = characters.get(partyId, userId)
            try {
                character.updatePoints(mutation(character.points))
                characters.save(partyId, character)
            } catch (e: IllegalArgumentException) {
            }
        }
    }
}