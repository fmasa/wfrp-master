package cz.frantisekmasa.wfrp_master.common.compendium.disease

import cz.frantisekmasa.wfrp_master.common.compendium.CharacterItemCompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.DiseaseRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Disease as CharacterDisease

class DiseaseCompendiumScreenModel(
    partyId: PartyId,
    firestore: FirebaseFirestore,
    compendium: Compendium<Disease>,
    characterItems: DiseaseRepository,
    parties: PartyRepository,
) : CharacterItemCompendiumItemScreenModel<Disease, CharacterDisease>(
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
        existing: CharacterDisease,
        new: CharacterDisease,
    ) {
        characterItems.save(transaction, characterId, new)
    }
}
