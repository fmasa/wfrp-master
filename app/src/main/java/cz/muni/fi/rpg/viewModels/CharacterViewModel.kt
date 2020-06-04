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
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import kotlinx.coroutines.*
import java.util.UUID
import kotlin.math.min

class CharacterViewModel(
    private val characters: CharacterRepository,
    private val inventoryItems: InventoryItemRepository,
    private val skillRepository: SkillRepository,
    val characterId: CharacterId
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    val character = characters.getLive(characterId.partyId, characterId.userId)
    val skills = skillRepository.forCharacter(characterId)

    val inventory: LiveData<List<InventoryItem>> = inventoryItems.findAllForCharacter(characterId)

    fun incrementWounds() = updatePoints { it.copy(wounds = it.wounds + 1) }
    fun decrementWounds() = updatePoints { it.copy(wounds = it.wounds - 1) }
    fun incrementFortunePoints() = updatePoints { it.copy(fortune = it.fortune + 1) }
    fun decrementFortunePoints() = updatePoints { it.copy(fortune = it.fortune - 1) }

    fun incrementFatePoints() = addFatePoints(1)
    fun decrementFatePoints() = addFatePoints(-1)

    fun incrementInsanityPoints() = updatePoints { it.copy(insanity = it.insanity + 1) }
    fun decrementInsanityPoints() = updatePoints { it.copy(insanity = it.insanity - 1) }

    suspend fun saveSkill(skill: Skill) = skillRepository.save(characterId, skill)

    suspend fun removeSkill(skill: Skill) = skillRepository.remove(characterId, skill.id)

    suspend fun addMoney(amount: Money) {
        val character = characters.get(characterId.partyId, characterId.userId)
        try {
            character.addMoney(amount)
            characters.save(characterId.partyId, character)
        } catch (e: IllegalArgumentException) {
        }
    }

    /**
     * @throws NotEnoughMoney
     */
    suspend fun subtractMoney(amount: Money) {
        val character = characters.get(characterId.partyId, characterId.userId)
        character.subtractMoney(amount)
        characters.save(characterId.partyId, character)
    }

    private fun addFatePoints(addition: Int) = updatePoints { it.updateFate(it.fate + addition) }

    private fun updatePoints(mutation: (points: Points) -> Points) {
        launch {
            val character = characters.get(characterId.partyId, characterId.userId)
            try {
                character.updatePoints(mutation(character.getPoints()))
                characters.save(characterId.partyId, character)
            } catch (e: IllegalArgumentException) {
            }
        }
    }

    suspend fun saveInventoryItem(inventoryItem: InventoryItem) {
        inventoryItems.save(characterId, inventoryItem)
    }

    suspend fun removeInventoryItem(inventoryItem: InventoryItem) {
        inventoryItems.remove(characterId, inventoryItem.id)
    }
}