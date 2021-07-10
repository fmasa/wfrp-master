package cz.frantisekmasa.wfrp_master.core.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

abstract class CharacterItemViewModel<TItem : CharacterItem, TCompendiumItem : CompendiumItem>(
    private val characterId: CharacterId,
    private val repository: CharacterItemRepository<TItem>,
    private val compendium: Compendium<TCompendiumItem>,
) : ViewModel() {
    private val itemsFlow = repository.findAllForCharacter(characterId)
    private val compendiumItems by lazy { compendium.liveForParty(characterId.partyId) }

    val items: LiveData<List<TItem>> = itemsFlow.asLiveData()

    val notUsedItemsFromCompendium: LiveData<List<TCompendiumItem>> by lazy {
        compendiumItems.combineTransform(itemsFlow) { compendiumItems, characterItems ->
            val itemsUsedByCharacter = characterItems.mapNotNull { it.compendiumId }.toSet()

            emit(compendiumItems.filter { !itemsUsedByCharacter.contains(it.id) })
        }.asLiveData()
    }

    val compendiumItemsCount: LiveData<Int> by lazy {
        compendiumItems.map { it.size }.asLiveData()
    }

    suspend fun saveItem(item: TItem) {
        withContext(Dispatchers.IO) {
            repository.save(characterId, item)
        }
    }

    suspend fun removeItem(item: TItem) {
        withContext(Dispatchers.IO) {
            repository.remove(characterId, item.id)
        }
    }
}
