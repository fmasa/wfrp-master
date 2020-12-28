package cz.frantisekmasa.wfrp_master.core.domain.party

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import cz.frantisekmasa.wfrp_master.core.connectivity.CouldNotConnectToBackend
import java.util.*

interface PartyRepository {

    /**
     * @throws CouldNotConnectToBackend when repository cannot connect to server
     */
    suspend fun save(party: Party)

    /**
     * @throws PartyNotFound
     */
    suspend fun get(id: UUID): Party

    fun getLive(id: UUID): Flow<Either<PartyNotFound, Party>>

    /**
     * Creates RecyclerView Adapter with parties that user has access to
     */
    fun forUserLive(userId: String): Flow<List<Party>>

    suspend fun forUser(userId: String): List<Party>
}