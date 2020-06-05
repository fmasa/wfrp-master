package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository

class SkillsViewModel(
    private val characterId: CharacterId,
    private val skillRepository: SkillRepository
) : ViewModel() {
    val skills: LiveData<List<Skill>> = skillRepository.forCharacter(characterId)

    suspend fun saveSkill(skill: Skill) = skillRepository.save(characterId, skill)

    suspend fun removeSkill(skill: Skill) = skillRepository.remove(characterId, skill.id)
}