package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.domain.talents.TalentRepository

class TalentsViewModel(
    private val characterId: CharacterId,
    private val talentRepository: TalentRepository
) : ViewModel() {
    val talents: LiveData<List<Talent>> = talentRepository.findAllForCharacter(characterId)

    suspend fun saveTalent(talent: Talent) = talentRepository.save(characterId, talent)

    suspend fun removeTalent(talent: Talent) = talentRepository.remove(characterId, talent.id)
}