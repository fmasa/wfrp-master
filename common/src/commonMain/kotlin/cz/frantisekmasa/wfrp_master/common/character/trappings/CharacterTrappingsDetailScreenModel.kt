package cz.frantisekmasa.wfrp_master.common.character.trappings

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.journal.rules.TrappingJournalProvider
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class CharacterTrappingsDetailScreenModel(
    characterId: CharacterId,
    partyRepository: PartyRepository,
    userProvider: UserProvider,
    private val inventoryItems: InventoryItemRepository,
    characters: CharacterRepository,
    private val trappingSaver: TrappingSaver,
    private val trappingJournalProvider: TrappingJournalProvider,
) : CharacterItemScreenModel<InventoryItem>(
        characterId,
        inventoryItems,
        userProvider,
        partyRepository,
    ) {
    private val character = characters.getLive(characterId).right()

    val inventory: Flow<List<TrappingItem>> =
        inventoryItems.findAllForCharacter(characterId)
            .map { items ->
                val (storedItems, notStoredItems) = items.partition { it.containerId != null }
                val storedItemsByContainer = storedItems.groupBy { it.containerId }

                notStoredItems.map { item ->
                    val type = item.trappingType

                    if (type is TrappingType.Container) {
                        TrappingItem.Container(
                            item,
                            type,
                            storedItemsByContainer[item.id] ?: emptyList(),
                        )
                    } else {
                        TrappingItem.SeparateTrapping(item)
                    }
                }
            }

    val strengthBonus: Flow<Int> =
        character
            .map { it.characteristics.strengthBonus }
            .distinctUntilChanged()

    override suspend fun saveItem(item: InventoryItem) {
        trappingSaver.saveInventoryItem(characterId, item)
    }

    override suspend fun removeItem(item: InventoryItem) {
        trappingSaver.removeInventoryItem(characterId, item)
    }

    suspend fun removeFromContainer(trapping: InventoryItem) {
        Reporting.record { trappingRemovedFromContainer() }
        inventoryItems.save(characterId, trapping.copy(containerId = null))
    }

    fun getTrappingDetail(trappingId: Uuid) =
        getItem(trappingId)
            .combine(trappingJournalProvider.getTrappingJournal(characterId.partyId)) { trappingOrError, trappingJournal ->
                trappingOrError.map { CharacterTrappingDetailScreenState(it, trappingJournal) }
            }

    /**
     * Returns true when trapping was added to container and false otherwise
     */
    suspend fun addToContainer(
        trapping: InventoryItem,
        container: InventoryItem,
    ) {
        Reporting.record { trappingAddedToContainer() }
        trappingSaver.addToContainer(characterId, trapping, container)
    }
}
