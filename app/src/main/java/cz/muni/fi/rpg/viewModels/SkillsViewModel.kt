package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.frantisekmasa.wfrp_master.compendium.domain.Compendium
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill as CompendiumSkill
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class SkillsViewModel(
    private val characterId: CharacterId,
    private val skillRepository: SkillRepository,
    private val compendium: Compendium<CompendiumSkill>
) : ViewModel() {

    val skills: Flow<List<Skill>> = skillRepository.forCharacter(characterId)
    val compendiumSkillsCount: Flow<Int> by lazy { compendiumSkills.map { it.size } }
    val notUsedSkillsFromCompendium: Flow<List<CompendiumSkill>> by lazy {
        compendiumSkills.zip(skills) { compendiumSkills, characterSkills ->
            val skillsUsedByCharacter = characterSkills.mapNotNull { it.compendiumId }.toSet()
            compendiumSkills.filter { !skillsUsedByCharacter.contains(it.id) }
        }
    }

    private val compendiumSkills by lazy { compendium.liveForParty(characterId.partyId) }

    suspend fun saveSkill(skill: Skill) = skillRepository.save(characterId, skill)

    suspend fun saveCompendiumSkill(skillId: UUID, compendiumSkillId: UUID, advances: Int) {
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


    fun removeSkill(skill: Skill) = viewModelScope.launch(Dispatchers.IO) {
        skillRepository.remove(characterId, skill.id)
    }
}