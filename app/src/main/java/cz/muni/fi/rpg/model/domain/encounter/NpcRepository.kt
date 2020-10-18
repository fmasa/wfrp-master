package cz.muni.fi.rpg.model.domain.encounter

import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import kotlinx.coroutines.flow.Flow

interface NpcRepository {
    fun findByEncounter(encounterId: EncounterId): Flow<List<Npc>>

    /**
     * @throws CombatantNotFound
     */
    suspend fun get(id: NpcId): Npc

    /**
     * Creates or updates given NPC
     */
    suspend fun save(encounterId: EncounterId, vararg npcs: Npc)

    /**
     * Removes NPC if she exists or does nothing
     */
    suspend fun remove(id: NpcId)

    /**
     * Returns value that can be used for new NPC so that it's sorted at the end
     */
    suspend fun getNextPosition(encounterId: EncounterId): Int
}