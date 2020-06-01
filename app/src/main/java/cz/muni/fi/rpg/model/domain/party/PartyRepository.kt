package cz.muni.fi.rpg.model.domain.party

import androidx.lifecycle.LiveData
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
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

    fun getLive(id: UUID): LiveData<Either<PartyNotFound, Party>>

    /**
     * Creates RecyclerView Adapter with parties that user has access to
     */
    fun forUser(userId: String): LiveData<List<Party>>
}