package cz.frantisekmasa.wfrp_master.common.core.domain.character

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import kotlinx.coroutines.flow.Flow

/**
 * @see [CharacterItem]
 */
interface CharacterItemRepository<T : CharacterItem> {
    /**
     * Returns flow which emits current list of items for given character
     */
    fun findAllForCharacter(characterId: CharacterId): Flow<List<T>>

    /**
     * Removes given skill item character's skill list
     * or does nothing if given item is not associated to user
     */
    suspend fun remove(characterId: CharacterId, itemId: Uuid)

    /**
     * Inserts item to character's item list or updates it if it already exists
     */
    suspend fun save(characterId: CharacterId, item: T)
}
