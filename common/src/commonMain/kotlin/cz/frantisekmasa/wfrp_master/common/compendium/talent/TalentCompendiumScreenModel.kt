package cz.frantisekmasa.wfrp_master.common.compendium.talent

import cz.frantisekmasa.wfrp_master.common.character.effects.EffectManager
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectSource
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent as CharacterTalent

class TalentCompendiumScreenModel(
    partyId: PartyId,
    firestore: Firestore,
    compendium: Compendium<Talent>,
    characterItems: CharacterItemRepository<CharacterTalent>,
    private val effectManager: EffectManager,
) : CompendiumItemScreenModel<Talent, CharacterTalent>(
    partyId,
    firestore,
    compendium,
    characterItems
) {

    override suspend fun saveCharacterItem(
        transaction: Transaction,
        characterId: CharacterId,
        existing: CharacterTalent,
        new: CharacterTalent
    ) {
        effectManager.saveEffectSource(transaction, characterId, EffectSource.Talent(new))
    }
}
