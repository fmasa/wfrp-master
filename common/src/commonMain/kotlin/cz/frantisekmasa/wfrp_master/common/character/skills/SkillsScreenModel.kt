package cz.frantisekmasa.wfrp_master.common.character.skills

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill as CompendiumSkill

class SkillsScreenModel(
    private val characterId: CharacterId,
    private val skillRepository: SkillRepository,
    private val compendium: Compendium<CompendiumSkill>
) : ScreenModel {
    val skills: Flow<List<Skill>> = skillRepository.findAllForCharacter(characterId)

    val compendiumSkillsCount: Flow<Int> by lazy {
        compendiumSkills.map { it.size }
    }
    val notUsedSkillsFromCompendium: Flow<List<CompendiumSkill>> by lazy {
        compendiumSkills.combineTransform(skills) { compendiumSkills, characterSkills ->
            val skillsUsedByCharacter = characterSkills.mapNotNull { it.compendiumId }.toSet()

            emit(compendiumSkills.filter { !skillsUsedByCharacter.contains(it.id) })
        }
    }

    private val compendiumSkills by lazy { compendium.liveForParty(characterId.partyId) }

    suspend fun saveSkill(skill: Skill) = skillRepository.save(characterId, skill)

    suspend fun saveCompendiumSkill(skillId: Uuid, compendiumSkillId: Uuid, advances: Int) {
        val compendiumSkill = compendium.getItem(
            partyId = characterId.partyId,
            itemId = compendiumSkillId,
        )

        skillRepository.save(
            characterId,
            Skill(
                id = skillId,
                compendiumId = compendiumSkill.id,
                advanced = compendiumSkill.advanced,
                characteristic = compendiumSkill.characteristic,
                name = compendiumSkill.name,
                description = compendiumSkill.description,
                advances = advances,
            )
        )
    }

    fun removeSkill(skill: Skill) = coroutineScope.launch(Dispatchers.IO) {
        skillRepository.remove(characterId, skill.id)
    }
}
