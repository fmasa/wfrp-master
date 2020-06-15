package cz.muni.fi.rpg.model.domain.spells

import androidx.lifecycle.LiveData
import cz.muni.fi.rpg.model.domain.character.CharacterId
import java.util.*

interface SpellRepository {

    /**
     * Returns observable list of spells for given character
     *
     * Note: Current value MAY NOT be set immediately as LiveData value
     */
    fun findAllForCharacter(characterId: CharacterId): LiveData<List<Spell>>

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