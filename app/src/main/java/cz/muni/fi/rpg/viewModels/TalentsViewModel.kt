package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.domain.talents.TalentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TalentsViewModel(
    private val characterId: CharacterId,
    private val talentRepository: TalentRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val talents: Flow<List<Talent>> = talentRepository.findAllForCharacter(characterId)

    suspend fun saveTalent(talent: Talent) = talentRepository.save(characterId, talent)

    fun removeTalent(talent: Talent) = launch { talentRepository.remove(characterId, talent.id) }
}