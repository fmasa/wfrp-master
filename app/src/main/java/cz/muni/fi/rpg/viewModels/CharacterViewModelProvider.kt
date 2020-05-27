package cz.muni.fi.rpg.viewModels

import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemRepository
import java.util.UUID
import javax.inject.Inject

class CharacterViewModelProvider @Inject constructor(
    private val characters: CharacterRepository,
    private val inventory: InventoryItemRepository
) {
    fun factory(partyId: UUID, userId: String) = FixedViewModelFactory(
        CharacterViewModel(characters, inventory, partyId, userId)
    )
}