package cz.muni.fi.rpg.model.domain.talents

import androidx.lifecycle.LiveData
import cz.muni.fi.rpg.model.domain.character.CharacterId
import java.util.*

interface TalentRepository {

    /**
     * Returns observable list of talent for given character
     *
     * Note: Current value MAY NOT be set immediately as LiveData value
     */
    fun findAllForCharacter(characterId: CharacterId): LiveData<List<Talent>>

    /**
     * Removes given talent from character's talent list
     * or does nothing if given talent is not associated to user
     */
    suspend fun remove(characterId: CharacterId, talentId: UUID)

    /**
     * Inserts talent to character's talent list or updates it if it already exists
     */
    suspend fun save(characterId: CharacterId, talent: Talent)
}