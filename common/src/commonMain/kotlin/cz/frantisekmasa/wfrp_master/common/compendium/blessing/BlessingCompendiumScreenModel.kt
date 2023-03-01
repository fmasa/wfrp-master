package cz.frantisekmasa.wfrp_master.common.compendium.blessing

import cz.frantisekmasa.wfrp_master.common.compendium.CharacterItemCompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing as CharacterBlessing

class BlessingCompendiumScreenModel(
    partyId: PartyId,
    firestore: Firestore,
    compendium: Compendium<Blessing>,
    characterItems: CharacterItemRepository<CharacterBlessing>,
) : CharacterItemCompendiumItemScreenModel<Blessing, CharacterBlessing>(
    partyId,
    firestore,
    compendium,
    characterItems
) {

    override suspend fun saveCharacterItem(
        transaction: Transaction,
        characterId: CharacterId,
        existing: CharacterBlessing,
        new: CharacterBlessing
    ) {
        characterItems.save(transaction, characterId, new)
    }
}
