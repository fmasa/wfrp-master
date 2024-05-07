package cz.frantisekmasa.wfrp_master.common.compendium.blessing

import cz.frantisekmasa.wfrp_master.common.compendium.CharacterItemCompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing as CharacterBlessing

class BlessingCompendiumScreenModel(
    partyId: PartyId,
    firestore: FirebaseFirestore,
    compendium: Compendium<Blessing>,
    characterItems: CharacterItemRepository<CharacterBlessing>,
    parties: PartyRepository,
) : CharacterItemCompendiumItemScreenModel<Blessing, CharacterBlessing>(
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
        existing: CharacterBlessing,
        new: CharacterBlessing,
    ) {
        characterItems.save(transaction, characterId, new)
    }
}
