package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cz.frantisekmasa.wfrp_master.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent as CompendiumTalent
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.domain.talents.TalentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class TalentsViewModel(
    private val characterId: CharacterId,
    private val talentRepository: TalentRepository,
    private val compendium: Compendium<CompendiumTalent>,
) : ViewModel() {

    private val talentsFlow = talentRepository.findAllForCharacter(characterId)
    val talents: LiveData<List<Talent>> = talentsFlow.asLiveData()
    val compendiumTalentsCount: LiveData<Int> by lazy { compendiumTalents.map { it.size }.asLiveData() }

    val notUsedTalentsFromCompendium: LiveData<List<CompendiumTalent>> by lazy {
        compendiumTalents.combineTransform(talentsFlow) { compendiumTalents, characterTalents ->
            val talentsUsedByCharacter = characterTalents.mapNotNull { it.compendiumId }.toSet()

            emit(compendiumTalents.filter { !talentsUsedByCharacter.contains(it.id) })
        }.asLiveData()
    }

    private val compendiumTalents = compendium.liveForParty(characterId.partyId)

    suspend fun saveTalent(talent: Talent) = talentRepository.save(characterId, talent)

    fun removeTalent(talent: Talent) = viewModelScope.launch(Dispatchers.IO) {
        talentRepository.remove(characterId, talent.id)
    }

    suspend fun saveCompendiumTalent(talentId: UUID, compendiumTalentId: UUID, timesTaken: Int) {
        val compendiumTalent = compendium.getItem(
            partyId = characterId.partyId,
            itemId = compendiumTalentId,
        )

        talentRepository.save(
            characterId,
            Talent(
                id = talentId,
                compendiumId = compendiumTalent.id,
                name = compendiumTalent.name,
                description = compendiumTalent.description,
                taken = timesTaken,
            )
        )
    }
}