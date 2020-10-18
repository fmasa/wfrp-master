package cz.muni.fi.rpg.model.domain.compendium

import kotlinx.coroutines.flow.Flow
import java.util.*

interface Compendium<T : CompendiumItem> {

    fun liveForParty(partyId: UUID): Flow<List<T>>

    /**
     * Updates item if it exists and creates it if it doesn't
     */
    suspend fun saveItem(partyId: UUID, item: T)

    /**
     * Removes item if it's persisted,
     * otherwise does nothing.
     */
    suspend fun remove(partyId: UUID, item: T)
}