package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.NotEnoughMoney
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.sum
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TrappingsScreenModel(
    private val characterId: CharacterId,
    private val inventoryItems: InventoryItemRepository,
    private val characters: CharacterRepository,
    private val firestore: Firestore,
) : ScreenModel {

    private val character = characters.getLive(characterId).right()
    val items = inventoryItems.findAllForCharacter(characterId)

    val inventory: Flow<List<Trapping>> = items.map { items ->
        val (storedItems, notStoredItems) = items.partition { it.containerId != null }
        val storedItemsByContainer = storedItems.groupBy { it.containerId }

        notStoredItems.map { item ->
            val type = item.trappingType

            if (type is TrappingType.Container)
                Trapping.Container(
                    item,
                    type,
                    storedItemsByContainer[item.id] ?: emptyList(),
                )
            else Trapping.SeparateTrapping(item)
        }
    }

    val strengthBonus: Flow<Int> = character
        .map { it.characteristics.strengthBonus }
        .distinctUntilChanged()

    val maxEncumbrance: Flow<Encumbrance> = character
        .map { Encumbrance.maximumForCharacter(it.characteristics) }
        .distinctUntilChanged()

    val totalEncumbrance: Flow<Encumbrance?> =
        items.map { items -> items.map { it.effectiveEncumbrance }.sum() }

    val money: Flow<Money> = character.map { it.money }

    @Immutable
    sealed interface Trapping {

        val item: InventoryItem
        val allItems: List<InventoryItem>

        @Immutable
        data class Container(
            override val item: InventoryItem,
            val container: TrappingType.Container,
            val storedTrappings: List<InventoryItem>,
        ) : Trapping {
            override val allItems: List<InventoryItem> = listOf(item) + storedTrappings
        }

        @Immutable
        data class SeparateTrapping(override val item: InventoryItem) : Trapping {
            override val allItems: List<InventoryItem> get() = listOf(item)
        }
    }

    suspend fun addMoney(amount: Money) {
        val character = characters.get(characterId)
        try {
            characters.save(characterId.partyId, character.addMoney(amount))
        } catch (e: IllegalArgumentException) {
        }
    }

    /**
     * @throws NotEnoughMoney
     */
    suspend fun subtractMoney(amount: Money) {
        val character = characters.get(characterId)
        characters.save(characterId.partyId, character.subtractMoney(amount))
    }

    suspend fun saveInventoryItem(inventoryItem: InventoryItem) {
        val itemsRemovedFromContainer = takeAllItemsFromContainer(inventoryItem)

        firestore.runTransaction { transaction ->
            inventoryItems.save(transaction, characterId, inventoryItem)

            if (inventoryItem.trappingType !is TrappingType.Container) {
                itemsRemovedFromContainer.forEach {
                    inventoryItems.save(transaction, characterId, it)
                }
            }
            characterId
        }
    }

    fun removeInventoryItem(inventoryItem: InventoryItem) = coroutineScope.launch(Dispatchers.IO) {
        val itemsPreviouslyStoredInContainer = takeAllItemsFromContainer(inventoryItem)

        firestore.runTransaction { transaction ->
            inventoryItems.remove(transaction, characterId, inventoryItem.id)

            itemsPreviouslyStoredInContainer.forEach {
                inventoryItems.save(transaction, characterId, it)
            }
        }
    }

    private suspend fun takeAllItemsFromContainer(possibleContainer: InventoryItem): List<InventoryItem> {
        return items.first()
            .filter { it.containerId == possibleContainer.id }
            .map { it.copy(containerId = null) }
    }

    suspend fun removeFromContainer(trapping: InventoryItem) {
        inventoryItems.save(characterId, trapping.copy(containerId = null))
    }

    /**
     * Returns true when trapping was added to container and false otherwise
     */
    suspend fun addToContainer(
        trapping: InventoryItem,
        container: InventoryItem,
    ) {
        Napier.d("Trying to store $trapping in $container")

        if (container.containerId != null || trapping.containerId == container.id) {
            return
        }

        val trappingType = trapping.trappingType
        val updatedTrappings = mutableListOf<InventoryItem>()

        // When storing Container X in a Container Y, all items previously stored
        // in X will be stored in Y
        if (trappingType is TrappingType.Container) {
            updatedTrappings.addAll(
                takeAllItemsFromContainer(trapping)
                    .map { it.addToContainer(container.id) }
            )
        }

        updatedTrappings += trapping.addToContainer(container.id)

        firestore.runTransaction { transaction ->
            updatedTrappings.forEach {
                inventoryItems.save(transaction, characterId, it)
            }
        }
    }
}
