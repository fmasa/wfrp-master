package cz.frantisekmasa.wfrp_master.common.compendium

import arrow.core.Either
import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Transaction
import kotlinx.coroutines.flow.Flow

abstract class CompendiumItemScreenModel<A : CompendiumItem<A>, B : CharacterItem<B, A>>(
    private val partyId: PartyId,
    private val firestore: Firestore,
    protected val compendium: Compendium<A>,
    protected val characterItems: CharacterItemRepository<B>
) : ScreenModel {

    val items: Flow<List<A>> = compendium.liveForParty(partyId)

    suspend fun createNew(compendiumItem: A) {
        firestore.runTransaction { transaction ->
            compendium.save(transaction, partyId, compendiumItem)
        }
    }

    suspend fun update(compendiumItem: A) {
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

    suspend fun import(actions: Sequence<ImportAction<A>>) {
        actions.forEach { action ->
            when (action) {
                is ImportAction.CreateNew -> createNew(action.item)
                is ImportAction.Update -> update(action.item)
            }
        }
    }

    sealed class ImportAction<T : CompendiumItem<T>>(val item: T) {
        class CreateNew<T : CompendiumItem<T>>(item: T) : ImportAction<T>(item)
        class Update<T : CompendiumItem<T>>(item: T) : ImportAction<T>(item)
    }

    suspend fun remove(compendiumItem: A) {
        val characterItems = characterItems.findByCompendiumId(partyId, compendiumItem.id)

        firestore.runTransaction { transaction ->
            compendium.remove(transaction, partyId, compendiumItem)
            characterItems.forEach { (characterId, item) ->
                saveCharacterItem(transaction, characterId, item, item.unlinkFromCompendium())
            }
        }
    }

    fun get(id: Uuid): Flow<Either<CompendiumItemNotFound, A>> {
        return compendium.getLive(partyId, id)
    }

    protected abstract suspend fun saveCharacterItem(
        transaction: Transaction,
        characterId: CharacterId,
        existing: B,
        new: B
    )
}
