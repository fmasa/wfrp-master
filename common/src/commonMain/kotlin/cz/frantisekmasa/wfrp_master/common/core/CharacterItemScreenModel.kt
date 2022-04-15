package cz.frantisekmasa.wfrp_master.common.core

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

abstract class CharacterItemScreenModel<
    TItem : CharacterItem,
    TCompendiumItem : CompendiumItem<TCompendiumItem>
    >(
    private val characterId: CharacterId,
    private val repository: CharacterItemRepository<TItem>,
    private val compendium: Compendium<TCompendiumItem>,
) : ScreenModel {

    private val compendiumItems by lazy { compendium.liveForParty(characterId.partyId) }

    val items: Flow<List<TItem>> = repository.findAllForCharacter(characterId)

    val notUsedItemsFromCompendium: Flow<List<TCompendiumItem>> by lazy {
        compendiumItems.combineTransform(items) { compendiumItems, characterItems ->
            val itemsUsedByCharacter = characterItems.mapNotNull { it.compendiumId }.toSet()

            emit(compendiumItems.filter { !itemsUsedByCharacter.contains(it.id) })
        }
    }

    val compendiumItemsCount: Flow<Int> by lazy { compendiumItems.map { it.size } }

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
