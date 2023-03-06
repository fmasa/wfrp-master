package cz.frantisekmasa.wfrp_master.common.compendium

import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterCompendiumItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction

abstract class CharacterItemCompendiumItemScreenModel<A : CompendiumItem<A>, B : CharacterItem<B, A>>(
    private val partyId: PartyId,
    private val firestore: Firestore,
    compendium: Compendium<A>,
    protected val characterItems: CharacterCompendiumItemRepository<B>
) : CompendiumItemScreenModel<A>(partyId, compendium) {

    override suspend fun createNew(compendiumItem: A) {
        firestore.runTransaction { transaction ->
            compendium.save(transaction, partyId, compendiumItem)
        }
    }

    override suspend fun update(compendiumItem: A) {
        val characterItems = characterItems.findByCompendiumId(partyId, compendiumItem.id)

        firestore.runTransaction { transaction ->
            compendium.save(transaction, partyId, compendiumItem)
            characterItems.forEach { (characterId, item) ->
                saveCharacterItem(
                    transaction,
                    characterId,
                    item,
                    item.updateFromCompendium(compendiumItem),
                )
            }
        }
    }

    override suspend fun remove(compendiumItem: A) {
        val characterItems = characterItems.findByCompendiumId(partyId, compendiumItem.id)

        firestore.runTransaction { transaction ->
            compendium.remove(transaction, partyId, compendiumItem)
            characterItems.forEach { (characterId, item) ->
                saveCharacterItem(
                    transaction,
                    characterId,
                    item,
                    item.unlinkFromCompendium(),
                )
            }
        }
    }

    protected abstract suspend fun saveCharacterItem(
        transaction: Transaction,
        characterId: CharacterId,
        existing: B,
        new: B
    )
}
