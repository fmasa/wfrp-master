package cz.frantisekmasa.wfrp_master.combat.domain.encounter

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import kotlinx.coroutines.flow.Flow
import java.util.*

interface EncounterRepository {
    /**
     * Returns current state of encounter
     *
     * @throws EncounterNotFound if encounter does not exist.
     */
    suspend fun get(id: EncounterId): Encounter

    /**
     * Returns Encounter as flow that emits when stored version changes
     */
    fun getLive(id: EncounterId): Flow<Either<EncounterNotFound, Encounter>>

    /**
     * Creates or updates encounter
     */
    suspend fun save(partyId: UUID, vararg encounters: Encounter)

    /**
     * Returns flow which emits current list of all encounters in party sorted by their position
     */
    fun findByParty(partyId: UUID): Flow<List<Encounter>>

    /**
     * Removes encounter if it exists or does nothing if it does not
     */
    suspend fun remove(id: EncounterId)

    /**
     * Returns value that can be used for new encounter so that it's sorted at the end
     */
    suspend fun getNextPosition(partyId: UUID): Int
}