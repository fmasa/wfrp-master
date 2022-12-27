package cz.frantisekmasa.wfrp_master.common.core.domain.character

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * @see [CharacterItem]
 */
interface CharacterItemRepository<T> {
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
     * Removes given skill item character's skill list
     * or does nothing if given item is not associated to user
     */
    fun remove(transaction: Transaction, characterId: CharacterId, itemId: Uuid)

    /**
     * Inserts item to character's item list or updates it if it already exists
     */
    suspend fun save(characterId: CharacterId, item: T)

    fun save(transaction: Transaction, characterId: CharacterId, item: T)

    suspend fun findByCompendiumId(
        partyId: PartyId,
        compendiumItemId: Uuid,
    ): List<Pair<CharacterId, T>>
}
