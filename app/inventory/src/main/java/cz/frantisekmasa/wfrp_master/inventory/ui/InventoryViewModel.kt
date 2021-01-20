package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.frantisekmasa.wfrp_master.core.domain.Armor
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.NotEnoughMoney
import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.core.utils.right
import cz.frantisekmasa.wfrp_master.inventory.domain.Encumbrance
import cz.frantisekmasa.wfrp_master.inventory.domain.sum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val characterId: CharacterId,
    private val inventoryItems: InventoryItemRepository,
    private val armorRepository: CharacterFeatureRepository<Armor>,
    private val characters: CharacterRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val character = characters.getLive(characterId).right()

    val inventory: StateFlow<List<InventoryItem>?> =
        inventoryItems.findAllForCharacter(characterId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val maxEncumbrance: StateFlow<Encumbrance?> =
        character.map { Encumbrance.maximumForCharacter(it.getCharacteristics()) }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val totalEncumbrance: StateFlow<Encumbrance?> = inventory
        .map { items -> items?.map { it.encumbrance * it.quantity }?.sum() }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val armor: Flow<Armor> = armorRepository.getLive(characterId).right()

    val money: Flow<Money> = character.map { it.getMoney() }

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

    fun removeInventoryItem(inventoryItem: InventoryItem) = launch {
        inventoryItems.remove(characterId, inventoryItem.id)
    }

    fun updateArmor(armor: Armor) = launch {
        armorRepository.save(characterId, armor)
    }
}