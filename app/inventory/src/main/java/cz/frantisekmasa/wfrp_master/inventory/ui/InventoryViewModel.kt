package cz.frantisekmasa.wfrp_master.inventory.ui

import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.flow.Flow
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
    val inventory: Flow<List<InventoryItem>> = inventoryItems.findAllForCharacter(characterId)

    val maxEncumbrance: Flow<Encumbrance> =
        character.map { Encumbrance.maximumForCharacter(it.getCharacteristics()) }

    val totalEncumbrance: Flow<Encumbrance?> = inventory
        .map { items -> items.map { it.effectiveEncumbrance }.sum() }

    val armor: Flow<EquippedArmour> =
        armorRepository
            .getLive(characterId)
            .right()
            .combine(inventory) { armour, items ->
                EquippedArmour(
                    armourFromItems = Armor.fromItems(items),
                    legacyArmour = armour,
                )
            }

    data class EquippedArmour(
        val armourFromItems: Armor,
        val legacyArmour: Armor,
    )

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

    fun removeInventoryItem(inventoryItem: InventoryItem) = viewModelScope.launch(Dispatchers.IO) {
        inventoryItems.remove(characterId, inventoryItem.id)
    }

    fun updateArmor(armor: Armor) = viewModelScope.launch(Dispatchers.IO) {
        armorRepository.save(characterId, armor)
    }
}
