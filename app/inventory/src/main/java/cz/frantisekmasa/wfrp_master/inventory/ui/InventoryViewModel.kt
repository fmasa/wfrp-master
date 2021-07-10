package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cz.frantisekmasa.wfrp_master.core.domain.Money
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.NotEnoughMoney
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.utils.right
import cz.frantisekmasa.wfrp_master.inventory.domain.Armor
import cz.frantisekmasa.wfrp_master.inventory.domain.Encumbrance
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.inventory.domain.sum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val characterId: CharacterId,
    private val inventoryItems: InventoryItemRepository,
    private val armorRepository: CharacterFeatureRepository<Armor>,
    private val characters: CharacterRepository
) : ViewModel() {

    private val character = characters.getLive(characterId).right()
    private val itemsFlow = inventoryItems.findAllForCharacter(characterId)
    val inventory: LiveData<List<InventoryItem>?> = itemsFlow.asLiveData()

    val maxEncumbrance: LiveData<Encumbrance> =
        character.map { Encumbrance.maximumForCharacter(it.getCharacteristics()) }.asLiveData()

    val totalEncumbrance: LiveData<Encumbrance?> = itemsFlow
        .map { items -> items.map { it.effectiveEncumbrance }.sum() }
        .asLiveData()

    val armor: LiveData<EquippedArmour> =
        armorRepository
            .getLive(characterId)
            .right()
            .combine(itemsFlow) { armour, items ->
                EquippedArmour(
                    armourFromItems = Armor.fromItems(items),
                    legacyArmour = armour,
                )
            }
            .asLiveData()

    data class EquippedArmour(
        val armourFromItems: Armor,
        val legacyArmour: Armor,
    )

    val money: LiveData<Money> = character.map { it.getMoney() }.asLiveData()

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

    fun removeInventoryItem(inventoryItem: InventoryItem) = viewModelScope.launch(Dispatchers.IO) {
        inventoryItems.remove(characterId, inventoryItem.id)
    }

    fun updateArmor(armor: Armor) = viewModelScope.launch(Dispatchers.IO) {
        armorRepository.save(characterId, armor)
    }
}
