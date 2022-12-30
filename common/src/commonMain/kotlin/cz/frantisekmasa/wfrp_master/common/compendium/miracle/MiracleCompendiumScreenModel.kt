package cz.frantisekmasa.wfrp_master.common.compendium.miracle

import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle as CharacterMiracle

class MiracleCompendiumScreenModel(
    partyId: PartyId,
    firestore: Firestore,
    compendium: Compendium<Miracle>,
    characterItems: CharacterItemRepository<CharacterMiracle>,
) : CompendiumItemScreenModel<Miracle, CharacterMiracle>(
    partyId,
    firestore,
    compendium,
    characterItems
) {

    override suspend fun saveCharacterItem(
        transaction: Transaction,
        characterId: CharacterId,
        existing: CharacterMiracle,
        new: CharacterMiracle
    ) {
        characterItems.save(transaction, characterId, new)
    }
}
