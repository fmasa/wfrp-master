package cz.muni.fi.rpg.model.domain.talents

import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TalentRepository {

    /**
     * Returns flow which emits current list of talent for given character
     */
    fun findAllForCharacter(characterId: CharacterId): Flow<List<Talent>>

    /**
     * Removes given talent from character's talent list
     * or does nothing if given talent is not associated to user or it does not exist
     */
    suspend fun remove(characterId: CharacterId, talentId: UUID)

    /**
     * Inserts talent to character's talent list or updates it if it already exists
     */
    suspend fun save(characterId: CharacterId, talent: Talent)
}