package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import java.util.UUID

class CharacterViewModel(characters: CharacterRepository, partyId: UUID, userId: String) :
    ViewModel() {
    val character = characters.getLive(partyId, userId)
}