package cz.frantisekmasa.wfrp_master.common.character.skills.addBasic

import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill as CompendiumSkill

class AddBasicSkillsScreenModel(
    private val characterId: CharacterId,
    private val skills: CharacterItemRepository<Skill>,
    compendium: Compendium<CompendiumSkill>,
) : ScreenModel {
    private val notAddedBasicSkills: Flow<List<CompendiumSkill>> =
        combine(
            compendium.liveForParty(characterId.partyId),
            skills.findAllForCharacter(characterId),
        ) { compendiumSkills, characterSkills ->
            val existingCompendiumIds =
                characterSkills
                    .asSequence()
                    .mapNotNull { it.compendiumId }
                    .toSet()

            compendiumSkills
                .asSequence()
                .filterNot { it.advanced }
                .filterNot { it.id in existingCompendiumIds }
                .toList()
        }

    val state: Flow<AddBasicSkillsScreenState> =
        notAddedBasicSkills.map {
            AddBasicSkillsScreenState(it.size)
        }

    suspend fun addBasicSkills() {
        // XXX: This should ideally be in transaction/batch, but it randomly crashes for some reason
        coroutineScope {
            notAddedBasicSkills.first()
                .map { skill ->
                    val id = uuid4()
                    Napier.d("Saving ${skill.name} as skill $id")

                    skills.save(
                        characterId,
                        Skill.fromCompendium(skill, 0),
                    )
                }
        }
    }
}
