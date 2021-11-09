package cz.frantisekmasa.wfrp_master.core.domain.character

import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import kotlinx.coroutines.flow.Flow
import java.util.UUID

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
    suspend fun remove(characterId: CharacterId, itemId: UUID)

    /**
     * Inserts item to character's item list or updates it if it already exists
     */
    suspend fun save(characterId: CharacterId, item: T)
}
