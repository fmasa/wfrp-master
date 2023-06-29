package cz.frantisekmasa.wfrp_master.common.character.talents

import cafe.adriel.voyager.core.model.coroutineScope
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectSource
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent as CompendiumTalent

class TalentsScreenModel(
    characterId: CharacterId,
    private val talentRepository: TalentRepository,
    private val compendium: Compendium<CompendiumTalent>,
    private val effectManager: EffectManager,
    private val firestore: Firestore,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
) : CharacterItemScreenModel<Talent, CompendiumTalent>(
    characterId,
    talentRepository,
    compendium,
    userProvider,
    partyRepository,
) {

    suspend fun saveTalent(talent: Talent) {
        firestore.runTransaction { transaction ->
            effectManager.saveEffectSource(transaction, characterId, EffectSource.Talent(talent))
        }
    }

    fun removeTalent(talent: Talent) = coroutineScope.launch(Dispatchers.IO) {
        firestore.runTransaction { transaction ->
            effectManager.removeEffectSource(transaction, characterId, EffectSource.Talent(talent))
        }
    }

    suspend fun saveCompendiumTalent(talentId: Uuid, compendiumTalentId: Uuid, timesTaken: Int) {
        val compendiumTalent = compendium.getItem(
            partyId = characterId.partyId,
            itemId = compendiumTalentId,
        )

        saveTalent(
            Talent(
                id = talentId,
                compendiumId = compendiumTalent.id,
                name = compendiumTalent.name,
                tests = compendiumTalent.tests,
                description = compendiumTalent.description,
                taken = timesTaken,
            )
        )
    }
}
