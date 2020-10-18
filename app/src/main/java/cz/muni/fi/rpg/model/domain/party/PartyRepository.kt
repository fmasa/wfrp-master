package cz.muni.fi.rpg.model.domain.party

import arrow.core.Either
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import kotlinx.coroutines.flow.Flow
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