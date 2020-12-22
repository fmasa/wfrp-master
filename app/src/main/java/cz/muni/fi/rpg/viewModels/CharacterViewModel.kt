package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import kotlinx.coroutines.*

class CharacterViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    val character = characters.getLive(characterId)

    suspend fun update(change: (Character) -> Unit) {
        val character = characters.get(characterId)

        change(character)

        characters.save(characterId.partyId, character)
    }

    suspend fun characterExists(): Boolean {
        return try {
            characters.get(characterId)

            true
        } catch (e: CharacterNotFound) {
            false
        }
    }
}