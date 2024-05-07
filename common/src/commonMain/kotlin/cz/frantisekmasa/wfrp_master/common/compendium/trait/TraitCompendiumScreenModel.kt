package cz.frantisekmasa.wfrp_master.common.compendium.trait

import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.compendium.CharacterItemCompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait as CharacterTrait

class TraitCompendiumScreenModel(
    partyId: PartyId,
    firestore: FirebaseFirestore,
    compendium: Compendium<Trait>,
    characterItems: CharacterItemRepository<CharacterTrait>,
    private val effectManager: EffectManager,
    parties: PartyRepository,
) : CharacterItemCompendiumItemScreenModel<Trait, CharacterTrait>(
        partyId,
        firestore,
        compendium,
        characterItems,
        parties,
    ) {
    override suspend fun updateCharacterItem(
        transaction: Transaction,
        party: Party,
        characterId: CharacterId,
        existing: CharacterTrait,
        new: CharacterTrait,
    ) {
        effectManager.saveItem(
            transaction,
            party,
            characterId,
            repository = characterItems,
            item = new,
            previousItemVersion = existing,
        )
    }
}
