package cz.frantisekmasa.wfrp_master.common.character.talents

import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore

class CharacterTalentDetailScreenModel(
    characterId: CharacterId,
    private val talentRepository: TalentRepository,
    private val effectManager: EffectManager,
    private val firestore: Firestore,
    userProvider: UserProvider,
    private val partyRepository: PartyRepository,
) : CharacterItemScreenModel<Talent>(
    characterId,
    talentRepository,
    userProvider,
    partyRepository,
) {

    suspend fun updateTalent(talent: Talent, existingTalent: Talent?) {
        firestore.runTransaction { transaction ->
            effectManager.saveItem(
                transaction,
                partyRepository.get(characterId.partyId),
                characterId,
                talentRepository,
                item = talent,
                previousItemVersion = existingTalent,
            )
        }
    }
}
