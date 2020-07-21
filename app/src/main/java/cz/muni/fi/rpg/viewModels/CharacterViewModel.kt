package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import kotlinx.coroutines.*

class CharacterViewModel(
    characterId: CharacterId,
    characters: CharacterRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    val character = characters.getLive(characterId)
}