package cz.frantisekmasa.wfrp_master.common.compendium

import arrow.core.Either
import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import kotlinx.coroutines.flow.Flow

abstract class CompendiumItemScreenModel<A : CompendiumItem<A>>(
    private val partyId: PartyId,
    protected val compendium: Compendium<A>,
) : ScreenModel {
    val items: Flow<List<A>> = compendium.liveForParty(partyId)

    suspend fun createNew(compendiumItem: A) {
        createNew(listOf(compendiumItem))
    }

    private suspend fun createNew(compendiumItems: List<A>) {
        compendium.saveItems(partyId, compendiumItems)
    }

    abstract suspend fun update(compendiumItem: A)

    suspend fun import(actions: List<ImportAction<A>>) {
        val newItems = mutableListOf<A>()
        val updatedItems = mutableListOf<A>()

        actions.forEach { action ->
            when (action) {
                is ImportAction.CreateNew -> newItems += action.item
                is ImportAction.Update -> updatedItems += action.item
            }
        }

        createNew(newItems)
        updatedItems.forEach { item -> update(item) }
    }

    sealed class ImportAction<T : CompendiumItem<T>>(val item: T) {
        class CreateNew<T : CompendiumItem<T>>(item: T) : ImportAction<T>(item)

        class Update<T : CompendiumItem<T>>(item: T) : ImportAction<T>(item)
    }

    abstract suspend fun remove(compendiumItem: A)

    fun get(id: Uuid): Flow<Either<CompendiumItemNotFound, A>> {
        return compendium.getLive(partyId, id)
    }
}
