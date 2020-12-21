package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.compendium.Compendium
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.domain.compendium.Talent as CompendiumTalent
import cz.muni.fi.rpg.model.domain.talents.TalentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.util.*

class TalentsViewModel(
    private val characterId: CharacterId,
    private val talentRepository: TalentRepository,
    private val compendium: Compendium<CompendiumTalent>,
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO) {

    val talents: Flow<List<Talent>> = talentRepository.findAllForCharacter(characterId)
    val compendiumTalentsCount: Flow<Int> by lazy { compendiumTalents.map { it.size } }

    val notUsedTalentsFromCompendium: Flow<List<CompendiumTalent>> by lazy {
        compendiumTalents.zip(talents) { compendiumTalents, characterTalents ->
            val talentsUsedByCharacter = characterTalents.mapNotNull { it.compendiumId }.toSet()
            compendiumTalents.filter { !talentsUsedByCharacter.contains(it.id) }
        }
    }

    private val compendiumTalents = compendium.liveForParty(characterId.partyId)

    suspend fun saveTalent(talent: Talent) = talentRepository.save(characterId, talent)

    fun removeTalent(talent: Talent) = launch { talentRepository.remove(characterId, talent.id) }

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