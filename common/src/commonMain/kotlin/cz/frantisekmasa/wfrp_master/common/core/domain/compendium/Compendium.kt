package cz.frantisekmasa.wfrp_master.common.core.domain.compendium

import arrow.core.Either
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow

interface Compendium<T : CompendiumItem<T>> {

    fun liveForParty(partyId: PartyId): Flow<List<T>>

    /**
     * @throws CompendiumItemNotFound
     */
    suspend fun getItem(partyId: PartyId, itemId: Uuid): T

    fun getLive(partyId: PartyId, itemId: Uuid): Flow<Either<CompendiumItemNotFound, T>>

    /**
     * Updates item if it exists and creates it if it doesn't
     */
    suspend fun saveItems(partyId: PartyId, items: List<T>)

    fun save(transaction: Transaction, partyId: PartyId, item: T)

    /**
     * Removes item if it's persisted,
     * otherwise does nothing.
     */
    suspend fun remove(transaction: Transaction, partyId: PartyId, item: T)
}
