package cz.frantisekmasa.wfrp_master.common.character.trappings.add

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.character.items.AvailableCompendiumItemsFactory
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class AddTrappingScreenModel(
    private val characterId: CharacterId,
    compendium: Compendium<Trapping>,
    private val trappings: InventoryItemRepository,
    availableCompendiumItemsFactory: AvailableCompendiumItemsFactory,
) : ScreenModel {

    val state: Flow<AddTrappingScreenState> = availableCompendiumItemsFactory.create(
        partyId = characterId.partyId,
        compendium = compendium,
        // Intentionally showing Trappings Character already has
        filterCharacterItems = flowOf(emptyList<InventoryItem>()),
    ).map { AddTrappingScreenState(it) }

    suspend fun saveTrapping(trapping: InventoryItem) {
        trappings.save(characterId, trapping)
    }
}
