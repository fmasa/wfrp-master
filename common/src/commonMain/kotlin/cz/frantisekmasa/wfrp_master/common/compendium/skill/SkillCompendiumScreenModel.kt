package cz.frantisekmasa.wfrp_master.common.compendium.skill

import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill as CharacterSkill

class SkillCompendiumScreenModel(
    partyId: PartyId,
    firestore: Firestore,
    compendium: Compendium<Skill>,
    characterItems: CharacterItemRepository<CharacterSkill>,
) : CompendiumItemScreenModel<Skill, CharacterSkill>(
    partyId,
    firestore,
    compendium,
    characterItems
) {

    override suspend fun saveCharacterItem(
        transaction: Transaction,
        characterId: CharacterId,
        existing: CharacterSkill,
        new: CharacterSkill
    ) {
        characterItems.save(transaction, characterId, new)
    }
}
