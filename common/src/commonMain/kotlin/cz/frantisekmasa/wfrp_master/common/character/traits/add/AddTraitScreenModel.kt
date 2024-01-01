package cz.frantisekmasa.wfrp_master.common.character.traits.add

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.map
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait as CompendiumTrait

class AddTraitScreenModel(
    private val characterId: CharacterId,
    private val traits: CharacterItemRepository<Trait>,
    compendium: Compendium<CompendiumTrait>,
    private val effectManager: EffectManager,
    private val firestore: FirebaseFirestore,
    private val parties: PartyRepository,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {
    val state = availableCompendiumItemsFactory.create(
        partyId = characterId.partyId,
        compendium = compendium,
        filterCharacterItems = traits.findAllForCharacter(characterId),
    ).map { AddTraitScreenState(availableCompendiumItems = it) }

    suspend fun saveNewTrait(trait: Trait) {
        firestore.runTransaction {
            effectManager.saveItem(
                this,
                parties.get(this, characterId.partyId),
                characterId,
                repository = traits,
                item = trait,
                previousItemVersion = null,
            )
        }
    }
}
