package cz.frantisekmasa.wfrp_master.common.compendium.miracle

import cz.frantisekmasa.wfrp_master.common.compendium.CharacterItemCompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle as CharacterMiracle

class MiracleCompendiumScreenModel(
    partyId: PartyId,
    firestore: FirebaseFirestore,
    compendium: Compendium<Miracle>,
    characterItems: CharacterItemRepository<CharacterMiracle>,
    parties: PartyRepository,
) : CharacterItemCompendiumItemScreenModel<Miracle, CharacterMiracle>(
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
        existing: CharacterMiracle,
        new: CharacterMiracle
    ) {
        characterItems.save(transaction, characterId, new)
    }
}
