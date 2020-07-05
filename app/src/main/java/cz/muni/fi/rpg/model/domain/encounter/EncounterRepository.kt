package cz.muni.fi.rpg.model.domain.encounter

import androidx.lifecycle.LiveData
import arrow.core.Either
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import java.util.*

interface EncounterRepository {
    /**
     * Returns current state of encounter
     *
     * @throws EncounterNotFound if encounter does not exist.
     */
    suspend fun get(id: EncounterId): Encounter

    /**
     * Returns Encounter as LiveData that are updated when stored version changes
     */
    fun getLive(id: EncounterId): LiveData<Either<EncounterNotFound, Encounter>>

    /**
     * Creates or updates encounter
     */
    suspend fun save(partyId: UUID, vararg encounters: Encounter)

    /**
     * Returns LiveData representation of all encounters in party sorted by their position
     */
    fun findByParty(partyId: UUID): LiveData<List<Encounter>>

    /**
     * Removes encounter if it exists or does nothing if it does not
     */
    suspend fun remove(id: EncounterId)

    /**
     * Returns value that can be used for new encounter so that it's sorted at the end
     */
    suspend fun getNextPosition(partyId: UUID): Int
}