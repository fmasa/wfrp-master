package cz.frantisekmasa.wfrp_master.common.character.trappings

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first

class TrappingSaver(
    private val trappings: InventoryItemRepository,
    private val firestore: FirebaseFirestore,
) {
    suspend fun addToContainer(
        characterId: CharacterId,
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
                takeAllItemsFromContainer(characterId, trapping)
                    .map { it.addToContainer(container.id) },
            )
        }

        updatedTrappings += trapping.addToContainer(container.id)

        firestore.runTransaction {
            updatedTrappings.forEach {
                trappings.save(this, characterId, it)
            }
        }
    }

    suspend fun saveInventoryItem(
        characterId: CharacterId,
        inventoryItem: InventoryItem,
    ) {
        val itemsRemovedFromContainer = takeAllItemsFromContainer(characterId, inventoryItem)

        firestore.runTransaction {
            trappings.save(this, characterId, inventoryItem)

            if (inventoryItem.trappingType !is TrappingType.Container) {
                itemsRemovedFromContainer.forEach {
                    trappings.save(this, characterId, it)
                }
            }
        }
    }

    suspend fun removeInventoryItem(
        characterId: CharacterId,
        inventoryItem: InventoryItem,
    ) {
        val itemsPreviouslyStoredInContainer = takeAllItemsFromContainer(characterId, inventoryItem)

        firestore.runTransaction {
            trappings.remove(this, characterId, inventoryItem.id)

            itemsPreviouslyStoredInContainer.forEach {
                trappings.save(this, characterId, it)
            }
        }
    }

    suspend fun takeAllItemsFromContainer(
        characterId: CharacterId,
        possibleContainer: InventoryItem,
    ): List<InventoryItem> {
        return trappings.findAllForCharacter(characterId).first()
            .filter { it.containerId == possibleContainer.id }
            .map { it.copy(containerId = null) }
    }
}
