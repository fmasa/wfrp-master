package cz.frantisekmasa.wfrp_master.common.character.talents

import cafe.adriel.voyager.core.model.coroutineScope
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent as CompendiumTalent

class TalentsScreenModel(
    private val characterId: CharacterId,
    private val talentRepository: TalentRepository,
    private val compendium: Compendium<CompendiumTalent>,
) : CharacterItemScreenModel<Talent, CompendiumTalent>(characterId, talentRepository, compendium) {

    suspend fun saveTalent(talent: Talent) = talentRepository.save(characterId, talent)

    fun removeTalent(talent: Talent) = coroutineScope.launch(Dispatchers.IO) {
        talentRepository.remove(characterId, talent.id)
    }

    suspend fun saveCompendiumTalent(talentId: Uuid, compendiumTalentId: Uuid, timesTaken: Int) {
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