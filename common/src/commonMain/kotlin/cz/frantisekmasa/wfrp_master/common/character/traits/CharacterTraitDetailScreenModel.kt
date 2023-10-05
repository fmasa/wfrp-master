package cz.frantisekmasa.wfrp_master.common.character.traits

import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.traits.dialog.TraitSpecificationsForm
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore

class CharacterTraitDetailScreenModel(
    characterId: CharacterId,
    private val traitRepository: TraitRepository,
    private val effectManager: EffectManager,
    private val firestore: Firestore,
    userProvider: UserProvider,
    private val partyRepository: PartyRepository,
) : CharacterItemScreenModel<Trait>(
    characterId,
    traitRepository,
    userProvider,
    partyRepository,
) {

    suspend fun saveTrait(
        trait: Trait,
        existingTrait: Trait?,
    ): TraitSpecificationsForm.SavingResult {
        firestore.runTransaction { transaction ->
            effectManager.saveItem(
                transaction,
                partyRepository.get(characterId.partyId),
                characterId,
                repository = traitRepository,
                item = trait,
                previousItemVersion = existingTrait,
            )
        }

        return TraitSpecificationsForm.SavingResult.SUCCESS
    }
}
