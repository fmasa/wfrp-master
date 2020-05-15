package cz.muni.fi.rpg.viewModels

import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemRepository
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import java.util.UUID
import javax.inject.Inject

class CharacterViewModelProvider @Inject constructor(
    private val characters: CharacterRepository,
    private val inventory: InventoryItemRepository,
    private val skills: SkillRepository
) {
    fun factory(partyId: UUID, userId: String) = FixedViewModelFactory(
        CharacterViewModel(characters, inventory, skills, partyId, userId)
    )
}