package cz.frantisekmasa.wfrp_master.common.compendium.spell

import cz.frantisekmasa.wfrp_master.common.compendium.CharacterItemCompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell as CharacterSpell

class SpellCompendiumScreenModel(
    partyId: PartyId,
    firestore: Firestore,
    compendium: Compendium<Spell>,
    characterItems: CharacterItemRepository<CharacterSpell>,
    parties: PartyRepository,
) : CharacterItemCompendiumItemScreenModel<Spell, CharacterSpell>(
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
        existing: CharacterSpell,
        new: CharacterSpell
    ) {
        characterItems.save(transaction, characterId, new)
    }
}
