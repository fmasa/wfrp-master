package cz.frantisekmasa.wfrp_master.common.encounters.domain

import arrow.core.Either
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import kotlinx.coroutines.flow.Flow

interface EncounterRepository {
    /**
     * Returns current state of encounter
     *
     * @throws EncounterNotFound if encounter does not exist.
     */
    suspend fun get(id: EncounterId): Encounter

    suspend fun find(id: EncounterId): Encounter? =
        try {
            get(id)
        } catch (e: EncounterNotFound) {
            null
        }

    /**
     * Returns Encounter as flow that emits when stored version changes
     */
    fun getLive(id: EncounterId): Flow<Either<EncounterNotFound, Encounter>>

    /**
     * Creates or updates encounter
     */
    suspend fun save(
        partyId: PartyId,
        vararg encounters: Encounter,
    )

    /**
     * Returns flow which emits current list of all encounters in party sorted by their position
     */
    fun findByParty(partyId: PartyId): Flow<List<Encounter>>

    /**
     * Removes encounter if it exists or does nothing if it does not
     */
    suspend fun remove(id: EncounterId)

    /**
     * Returns value that can be used for new encounter so that it's sorted at the end
     */
    suspend fun getNextPosition(partyId: PartyId): Int
}
