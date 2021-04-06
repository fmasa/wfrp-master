package cz.muni.fi.rpg.model.firestore.repositories

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import cz.frantisekmasa.wfrp_master.core.firestore.AggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.queryFlow
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItem
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItemId
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItemNotFound
import cz.frantisekmasa.wfrp_master.inventory.domain.InventoryItemRepository
import cz.muni.fi.rpg.model.firestore.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber

internal class FirestoreInventoryItemRepository(
    firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<InventoryItem>
) : InventoryItemRepository {
    private val parties = firestore.collection(COLLECTION_PARTIES)

    override fun findAllForCharacter(characterId: CharacterId) = queryFlow(
        inventoryItems(characterId).orderBy("name"),
        mapper,
    )

    override suspend fun save(characterId: CharacterId, item: InventoryItem) {
        val data = mapper.toDocumentData(item)
        Timber.d("Saving inventory item $data for character $characterId")

        inventoryItems(characterId)
            .document(item.id.toString())
            .set(data, SetOptions.merge())
            .await()
    }

    override suspend fun remove(characterId: CharacterId, itemId: InventoryItemId) {
        inventoryItems(characterId).document(itemId.toString()).delete().await()
    }

    private fun inventoryItems(characterId: CharacterId): CollectionReference {
        return parties.document(characterId.partyId.toString())
            .collection(COLLECTION_CHARACTERS)
            .document(characterId.id)
            .collection(COLLECTION_INVENTORY_ITEMS)
    }
}