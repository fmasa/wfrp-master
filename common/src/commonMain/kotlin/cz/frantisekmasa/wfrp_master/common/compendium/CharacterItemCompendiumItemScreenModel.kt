package cz.frantisekmasa.wfrp_master.common.compendium

import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Transaction

abstract class CharacterItemCompendiumItemScreenModel<A : CompendiumItem<A>, B : CharacterItem<B, A>>(
    private val partyId: PartyId,
    private val firestore: FirebaseFirestore,
    compendium: Compendium<A>,
    protected val characterItems: CharacterItemRepository<B>,
    private val parties: PartyRepository,
) : CompendiumItemScreenModel<A>(partyId, compendium) {

    override suspend fun update(compendiumItem: A) {
        val characterItems = characterItems.findByCompendiumId(partyId, compendiumItem.id)
        val party = parties.get(partyId)

        firestore.runTransaction {
            compendium.save(this, partyId, compendiumItem)
            characterItems.forEach { (characterId, item) ->
                updateCharacterItem(
                    this,
                    party,
                    characterId,
                    item,
                    item.updateFromCompendium(compendiumItem),
                )
            }
        }
    }

    override suspend fun remove(compendiumItem: A) {
        val characterItems = characterItems.findByCompendiumId(partyId, compendiumItem.id)
        val party = parties.get(partyId)

        firestore.runTransaction {
            compendium.remove(this, partyId, compendiumItem)
            characterItems.forEach { (characterId, item) ->
                updateCharacterItem(
                    this,
                    party,
                    characterId,
                    item,
                    item.unlinkFromCompendium(),
                )
            }
        }
    }

    protected abstract suspend fun updateCharacterItem(
        transaction: Transaction,
        party: Party,
        characterId: CharacterId,
        existing: B,
        new: B,
    )
}
