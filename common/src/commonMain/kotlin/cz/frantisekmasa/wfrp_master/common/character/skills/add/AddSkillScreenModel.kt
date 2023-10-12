package cz.frantisekmasa.wfrp_master.common.character.skills.add

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill as CompendiumSkill

class AddSkillScreenModel(
    private val characterId: CharacterId,
    private val skills: CharacterItemRepository<Skill>,
    private val compendium: Compendium<CompendiumSkill>,
    characters: CharacterRepository,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {
    val state = combine(
        availableCompendiumItemsFactory.create(
            characterId.partyId,
            compendium = compendium,
            filterCharacterItems = skills.findAllForCharacter(characterId),
        ),
        characters.getLive(characterId)
            .right()
            .map { it.characteristics }
            .distinctUntilChanged(),
    ) { compendiumItemChooserState, characteristics ->
        AddSkillScreenState(
            availableCompendiumItems = compendiumItemChooserState,
            characteristics = characteristics,
        )
    }

    suspend fun addSkill(skill: Skill) {
        skills.save(characterId, skill)
    }
}
