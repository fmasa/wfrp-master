package cz.frantisekmasa.wfrp_master.common.character.skills

import cafe.adriel.voyager.core.model.coroutineScope
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill as CompendiumSkill

class SkillsScreenModel(
    private val characterId: CharacterId,
    private val skillRepository: SkillRepository,
    private val compendium: Compendium<CompendiumSkill>,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
) : CharacterItemScreenModel<Skill, CompendiumSkill>(
    characterId,
    skillRepository,
    compendium,
    userProvider,
    partyRepository,
) {

    suspend fun saveSkill(skill: Skill) = skillRepository.save(characterId, skill)

    suspend fun addBasicSkills(skills: List<CompendiumSkill>) {
        // XXX: This should ideally be in transaction/batch, but it randomly crashes for some reason
        coroutineScope {
            skills.map { skill ->
                val id = uuid4()
                Napier.d("Saving ${skill.name} as skill $id")

                skillRepository.save(
                    characterId,
                    createSkillFromCompendium(id, skill, 0),
                )
            }
        }
    }

    suspend fun saveCompendiumSkill(skillId: Uuid, compendiumSkillId: Uuid, advances: Int) {
        val compendiumSkill = compendium.getItem(
            partyId = characterId.partyId,
            itemId = compendiumSkillId,
        )

        skillRepository.save(
            characterId,
            createSkillFromCompendium(skillId, compendiumSkill, advances),
        )
    }

    private fun createSkillFromCompendium(
        skillId: Uuid,
        compendiumSkill: CompendiumSkill,
        advances: Int,
    ): Skill {
        return Skill(
            id = skillId,
            compendiumId = compendiumSkill.id,
            advanced = compendiumSkill.advanced,
            characteristic = compendiumSkill.characteristic,
            name = compendiumSkill.name,
            description = compendiumSkill.description,
            advances = advances,
        )
    }

    suspend fun getBasicSkillsToImport(): List<CompendiumSkill> {
        return notUsedItemsFromCompendium.first()
            .filter { !it.advanced }
    }

    fun removeSkill(skill: Skill) = coroutineScope.launch(Dispatchers.IO) {
        skillRepository.remove(characterId, skill.id)
    }
}
