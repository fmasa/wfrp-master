package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.character.NotEnoughMoney
import cz.muni.fi.rpg.model.domain.character.Points
import cz.muni.fi.rpg.model.domain.common.Money
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.min

class CharacterViewModel(
    private val characters: CharacterRepository,
    private val inventoryItems: InventoryItemRepository,
    private val partyId: UUID,
    private val userId: String
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val characterId = CharacterId(partyId, userId)
    val character = characters.getLive(partyId, userId)

    val inventory: LiveData<List<InventoryItem>> = inventoryItems.findAllForCharacter(characterId)

    fun incrementWounds() = updatePoints { it.copy(wounds = it.wounds + 1) }
    fun decrementWounds() = updatePoints { it.copy(wounds = it.wounds - 1) }

    fun incrementFortunePoints() = updatePoints { it.copy(fortune = it.fortune + 1) }
    fun decrementFortunePoints() = updatePoints { it.copy(fortune = it.fortune - 1) }

    fun incrementFatePoints() = addFatePoints(1)
    fun decrementFatePoints() = addFatePoints(-1)

    fun incrementInsanityPoints() = updatePoints { it.copy(insanity = it.insanity + 1) }
    fun decrementInsanityPoints() = updatePoints { it.copy(insanity = it.insanity - 1) }

    suspend fun receiveMoney(amount: Money) {
        val character = characters.get(partyId, userId)
        try {
            character.receiveMoney(amount)
            characters.save(partyId, character)
        } catch (e: IllegalArgumentException) {
        }
    }

    /**
     * @throws NotEnoughMoney
     */
    suspend fun giveMoney(amount: Money) {
        val character = characters.get(partyId, userId)
        character.giveMoney(amount)
        characters.save(partyId, character)
    }

    private fun addFatePoints(addition: Int) = updatePoints {
        val newFatePoints = it.fate + addition

        it.copy(fate = newFatePoints, fortune = min(it.fortune, newFatePoints))
    }

    private fun updatePoints(mutation: (points: Points) -> Points) {
        launch {
            val character = characters.get(partyId, userId)
            try {
                character.updatePoints(mutation(character.getPoints()))
                characters.save(partyId, character)
            } catch (e: IllegalArgumentException) {
            }
        }
    }
}