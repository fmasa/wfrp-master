package cz.frantisekmasa.wfrp_master.common.character.traits

import cafe.adriel.voyager.core.model.coroutineScope
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.TraitRepository
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait as CompendiumTrait

class TraitsScreenModel(
    characterId: CharacterId,
    private val traitRepository: TraitRepository,
    private val compendium: Compendium<CompendiumTrait>,
    private val effectManager: EffectManager,
    private val firestore: Firestore,
    userProvider: UserProvider,
    private val partyRepository: PartyRepository,
) : CharacterItemScreenModel<Trait, CompendiumTrait>(
    characterId,
    traitRepository,
    compendium,
    userProvider,
    partyRepository,
) {

    fun removeTrait(trait: Trait) = coroutineScope.launch(Dispatchers.IO) {
        firestore.runTransaction { transaction ->
            effectManager.removeItem(
                transaction,
                partyRepository.get(characterId.partyId),
                characterId,
                traitRepository,
                trait,
            )
        }
    }

    suspend fun saveTrait(
        trait: Trait,
        existingTrait: Trait?,
    ) {
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
    }

    suspend fun saveNewTrait(
        compendiumTraitId: Uuid,
        specificationValues: Map<String, String>,
    ) {
        val compendiumTrait = compendium.getItem(
            partyId = characterId.partyId,
            itemId = compendiumTraitId,
        )

        saveTrait(
            trait = Trait(
                id = uuid4(),
                compendiumId = compendiumTrait.id,
                name = compendiumTrait.name,
                description = compendiumTrait.description,
                specificationValues = specificationValues.toMap(),
            ),
            existingTrait = null,
        )
    }
}
