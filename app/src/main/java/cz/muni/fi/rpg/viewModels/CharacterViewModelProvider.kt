package cz.muni.fi.rpg.viewModels

import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import java.util.UUID
import javax.inject.Inject

class CharacterViewModelProvider @Inject constructor(private val characters: CharacterRepository) {
    fun factory(partyId: UUID, userId: String) = FixedViewModelFactory(
        CharacterViewModel(characters, partyId, userId)
    )
}