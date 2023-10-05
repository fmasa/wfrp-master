package cz.frantisekmasa.wfrp_master.common.character.skills.add

import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.AdvancesForm
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
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

    suspend fun addCustomSkill(skill: Skill) {
        skills.save(characterId, skill)
    }

    suspend fun addCompendiumSkill(
        compendiumSkillId: Uuid,
        advances: Int,
    ): AdvancesForm.SavingResult {
        val compendiumSkill = try {
            compendium.getItem(
                partyId = characterId.partyId,
                itemId = compendiumSkillId,
            )
        } catch (e: CompendiumItemNotFound) {
            return AdvancesForm.SavingResult.COMPENDIUM_ITEM_WAS_REMOVED
        }

        skills.save(
            characterId,
            Skill.fromCompendium(compendiumSkill, advances),
        )

        return AdvancesForm.SavingResult.SUCCESS
    }
}
