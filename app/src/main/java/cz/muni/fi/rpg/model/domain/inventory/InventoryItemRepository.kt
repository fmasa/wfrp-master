package cz.muni.fi.rpg.model.domain.inventory

import cz.muni.fi.rpg.model.domain.character.CharacterId
import kotlinx.coroutines.flow.Flow

interface InventoryItemRepository {
    /**
     * Returns observable list of character's inventory items
     */
    fun findAllForCharacter(characterId: CharacterId): Flow<List<InventoryItem>>

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

    /**
     * Removes item from inventory or does nothing if given item is not in user's inventory
     */
    suspend fun remove(characterId: CharacterId, itemId: InventoryItemId)
}