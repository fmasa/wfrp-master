package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.CharacterFeatureRepository
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.character.NotEnoughMoney
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import cz.muni.fi.rpg.model.domain.common.Money
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemRepository
import cz.muni.fi.rpg.model.right

class InventoryViewModel(
    private val characterId: CharacterId,
    private val inventoryItems: InventoryItemRepository,
    private val armorRepository: CharacterFeatureRepository<Armor>,
    private val characters: CharacterRepository
) : ViewModel() {
    val inventory: LiveData<List<InventoryItem>> = inventoryItems.findAllForCharacter(characterId)

    val armor: LiveData<Either<CouldNotConnectToBackend, Armor>> =
        armorRepository.getLive(characterId)

    val money: LiveData<Money> = Transformations.map(characters.getLive(characterId).right()) {
        it.getMoney()
    }

    suspend fun addMoney(amount: Money) {
        val character = characters.get(characterId)
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
        val character = characters.get(characterId)
        character.subtractMoney(amount)
        characters.save(characterId.partyId, character)
    }

    suspend fun saveInventoryItem(inventoryItem: InventoryItem) {
        inventoryItems.save(characterId, inventoryItem)
    }

    suspend fun removeInventoryItem(inventoryItem: InventoryItem) {
        inventoryItems.remove(characterId, inventoryItem.id)
    }

    suspend fun updateArmor(armor: Armor) {
        armorRepository.save(characterId, armor)
    }
}