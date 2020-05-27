package cz.muni.fi.rpg.model.domain.inventory

import androidx.lifecycle.LiveData
import cz.muni.fi.rpg.model.domain.character.CharacterId

interface InventoryItemRepository {
    /**
     * Returns observable list of character's inventory items
     */
    fun findAllForCharacter(characterId: CharacterId): LiveData<List<InventoryItem>>

    /**
     * Inserts inventory item to character's inventory,
     * or updates it if item with same ID already exists
     */
    suspend fun save(characterId: CharacterId, item: InventoryItem)

    /**
     * Returns specified InventoryItem or throws an exception.
     *
     * @throws InventoryItemNotFound when inventory item does not exist.
     */
    suspend fun get(characterId: CharacterId, itemId: InventoryItemId): InventoryItem
}