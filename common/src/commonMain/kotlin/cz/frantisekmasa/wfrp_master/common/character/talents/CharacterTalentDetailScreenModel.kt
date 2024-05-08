package cz.frantisekmasa.wfrp_master.common.character.talents

import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.TalentRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore

class CharacterTalentDetailScreenModel(
    characterId: CharacterId,
    private val talentRepository: TalentRepository,
    private val effectManager: EffectManager,
    private val firestore: FirebaseFirestore,
    userProvider: UserProvider,
    private val partyRepository: PartyRepository,
) : CharacterItemScreenModel<Talent>(
        characterId,
        talentRepository,
        userProvider,
        partyRepository,
    ) {
    suspend fun updateTalent(
        talent: Talent,
        existingTalent: Talent?,
    ) {
        firestore.runTransaction {
            effectManager.saveItem(
                this,
                partyRepository.get(this, characterId.partyId),
                characterId,
                talentRepository,
                item = talent,
                previousItemVersion = existingTalent,
            )
        }
    }
}
