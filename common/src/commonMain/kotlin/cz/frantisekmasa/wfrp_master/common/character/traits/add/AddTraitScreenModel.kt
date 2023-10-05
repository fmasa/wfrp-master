package cz.frantisekmasa.wfrp_master.common.character.traits.add

import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.character.traits.dialog.TraitSpecificationsForm
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait as CompendiumTrait

class AddTraitScreenModel(
    private val characterId: CharacterId,
    private val traits: CharacterItemRepository<Trait>,
    private val compendium: Compendium<CompendiumTrait>,
    private val effectManager: EffectManager,
    private val firestore: Firestore,
    private val parties: PartyRepository,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {
    val state = availableCompendiumItemsFactory.create(
        partyId = characterId.partyId,
        compendium = compendium,
        filterCharacterItems = traits.findAllForCharacter(characterId),
    ).map { AddTraitScreenState(availableCompendiumItems = it) }

    suspend fun saveNewTrait(
        compendiumTraitId: Uuid,
        specificationValues: Map<String, String>,
    ): TraitSpecificationsForm.SavingResult {
        val compendiumTrait = try {
            compendium.getItem(
                partyId = characterId.partyId,
                itemId = compendiumTraitId,
            )
        } catch (e: CompendiumItemNotFound) {
            return TraitSpecificationsForm.SavingResult.COMPENDIUM_ITEM_WAS_REMOVED
        }

        firestore.runTransaction { transaction ->
            effectManager.saveItem(
                transaction,
                parties.get(characterId.partyId),
                characterId,
                repository = traits,
                item = Trait(
                    id = uuid4(),
                    compendiumId = compendiumTrait.id,
                    name = compendiumTrait.name,
                    description = compendiumTrait.description,
                    specificationValues = specificationValues.toMap(),
                ),
                previousItemVersion = null,
            )
        }

        return TraitSpecificationsForm.SavingResult.SUCCESS
    }
}
