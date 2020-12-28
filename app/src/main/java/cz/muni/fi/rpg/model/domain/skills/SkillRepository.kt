package cz.muni.fi.rpg.model.domain.skills

import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface SkillRepository {

    /**
     * Returns flow which emits current list of skills for given character
     */
    fun forCharacter(characterId: CharacterId): Flow<List<Skill>>

    /**
     * Removes given skill from character's skill list
     * or does nothing if given skill is not associated to user
     */
    suspend fun remove(characterId: CharacterId, skillId: UUID)

    /**
     * Inserts skill to character's skill list or updates it if it already exists
     */
    suspend fun save(characterId: CharacterId, skill: Skill)
}