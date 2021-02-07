package cz.frantisekmasa.wfrp_master.inventory.domain

import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import kotlinx.coroutines.flow.Flow

/* internal */ interface InventoryItemRepository {
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
     * Removes item from inventory or does nothing if given item is not in user's inventory
     */
    suspend fun remove(characterId: CharacterId, itemId: InventoryItemId)
}