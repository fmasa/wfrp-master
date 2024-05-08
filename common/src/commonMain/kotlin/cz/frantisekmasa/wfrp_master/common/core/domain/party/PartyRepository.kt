package cz.frantisekmasa.wfrp_master.common.core.domain.party

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import dev.gitlive.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow

interface PartyRepository {
    /**
     * @throws CouldNotConnectToBackend when repository cannot connect to server
     */
    suspend fun save(party: Party)

    fun save(
        transaction: Transaction,
        party: Party,
    )

    /**
     * @throws PartyNotFound
     */
    suspend fun update(
        id: PartyId,
        mutator: (Party) -> Party,
    )

    /**
     * @throws PartyNotFound
     */
    suspend fun get(
        transaction: Transaction,
        id: PartyId,
    ): Party

    fun getLive(id: PartyId): Flow<Either<PartyNotFound, Party>>

    /**
     * Creates RecyclerView Adapter with parties that user has access to
     */
    fun forUserLive(userId: UserId): Flow<List<Party>>

    suspend fun forUser(userId: UserId): List<Party>
}
