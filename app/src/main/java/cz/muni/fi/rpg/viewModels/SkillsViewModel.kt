package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SkillsViewModel(
    private val characterId: CharacterId,
    private val skillRepository: SkillRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val skills: LiveData<List<Skill>> = skillRepository.forCharacter(characterId)

    suspend fun saveSkill(skill: Skill) = skillRepository.save(characterId, skill)

    fun removeSkill(skill: Skill) = launch {
        skillRepository.remove(characterId, skill.id)
    }
}