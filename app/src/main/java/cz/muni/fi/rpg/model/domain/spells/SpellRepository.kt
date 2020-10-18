package cz.muni.fi.rpg.model.domain.spells

import cz.muni.fi.rpg.model.domain.character.CharacterId
import kotlinx.coroutines.flow.Flow
import java.util.*

interface SpellRepository {

    /**
     * Returns flow which emits current list of spells for given character
     */
    fun findAllForCharacter(characterId: CharacterId): Flow<List<Spell>>

    /**
     * Removes given spell from character's talent list
     * or does nothing if given talent is not associated to user or it does not exist
     */
    suspend fun remove(characterId: CharacterId, spellId: UUID)

    /**
     * Inserts spell to character's spell book or updates it if it already exists
     */
    suspend fun save(characterId: CharacterId, spell: Spell)
}