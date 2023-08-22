package cz.frantisekmasa.wfrp_master.common.compendium.trapping

import cz.frantisekmasa.wfrp_master.common.compendium.CharacterItemCompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction

class TrappingCompendiumScreenModel(
    partyId: PartyId,
    firestore: Firestore,
    compendium: Compendium<Trapping>,
    characterItems: CharacterItemRepository<InventoryItem>,
) : CharacterItemCompendiumItemScreenModel<Trapping, InventoryItem>(
    partyId,
    firestore,
    compendium,
    characterItems
) {

    override suspend fun saveCharacterItem(
        transaction: Transaction,
        characterId: CharacterId,
        existing: InventoryItem,
        new: InventoryItem,
    ) {
        characterItems.save(transaction, characterId, new)
    }
}
