package cz.muni.fi.rpg.model.domain.encounter

import androidx.lifecycle.LiveData
import cz.muni.fi.rpg.model.domain.encounters.EncounterId

interface CombatantRepository {
    fun findByEncounter(encounterId: EncounterId): LiveData<List<Combatant>>

    /**
     * @throws CombatantNotFound
     */
    suspend fun get(id: CombatantId): Combatant

    /**
     * Creates or updates given combatant
     */
    suspend fun save(encounterId: EncounterId, vararg combatants: Combatant)

    /**
     * Removes combatant if she exists or does nothing
     */
    suspend fun remove(id: CombatantId)

    /**
     * Returns value that can be used for new combatant so that it's sorted at the end
     */
    suspend fun getNextPosition(encounterId: EncounterId): Int
}