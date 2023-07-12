package cz.frantisekmasa.wfrp_master.common.compendium.trait

import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectSource
import cz.frantisekmasa.wfrp_master.common.compendium.CharacterItemCompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait as CharacterTrait

class TraitCompendiumScreenModel(
    partyId: PartyId,
    firestore: Firestore,
    compendium: Compendium<Trait>,
    characterItems: CharacterItemRepository<CharacterTrait>,
    private val effectManager: EffectManager,
) : CharacterItemCompendiumItemScreenModel<Trait, CharacterTrait>(partyId, firestore, compendium, characterItems) {

    override suspend fun saveCharacterItem(
        transaction: Transaction,
        characterId: CharacterId,
        existing: CharacterTrait,
        new: CharacterTrait
    ) {
        effectManager.saveEffectSource(
            transaction,
            characterId,
            source = EffectSource.Trait(new),
            previousSourceVersion = EffectSource.Trait(existing),
        )
    }
}
