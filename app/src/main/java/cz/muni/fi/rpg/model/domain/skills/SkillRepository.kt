package cz.muni.fi.rpg.model.domain.skills

import androidx.lifecycle.LiveData
import cz.muni.fi.rpg.model.domain.character.CharacterId
import java.util.UUID

interface SkillRepository {

    /**
     * Returns observable list of skills for given character
     *
     * Note: Current value MAY NOT be set immediately as LiveData value
     */
    fun forCharacter(characterId: CharacterId): LiveData<List<Skill>>

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