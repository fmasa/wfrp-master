package cz.frantisekmasa.wfrp_master.compendium.domain

import kotlinx.coroutines.flow.Flow
import cz.frantisekmasa.wfrp_master.compendium.domain.exceptions.CompendiumItemNotFound
import java.util.*

interface Compendium<T : CompendiumItem> {

    fun liveForParty(partyId: UUID): Flow<List<T>>

    /**
     * @throws CompendiumItemNotFound
     */
    suspend fun getItem(partyId: UUID, itemId: UUID): T

    /**
     * Updates item if it exists and creates it if it doesn't
     */
    suspend fun saveItems(partyId: UUID, vararg items: T)

    /**
     * Removes item if it's persisted,
     * otherwise does nothing.
     */
    suspend fun remove(partyId: UUID, item: T)
}