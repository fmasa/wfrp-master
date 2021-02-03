package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.utils.right


class CharacterViewModel(
    private val characterId: CharacterId,
    private val characters: CharacterRepository
) : ViewModel() {

    val character: LiveData<Character> = characters.getLive(characterId).right().asLiveData()

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