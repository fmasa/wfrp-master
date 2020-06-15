package cz.muni.fi.rpg.model.firestore.repositories

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemId
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemNotFound
import cz.muni.fi.rpg.model.domain.inventory.InventoryItemRepository
import cz.muni.fi.rpg.model.firestore.*
import kotlinx.coroutines.tasks.await

internal class FirestoreInventoryItemRepository(
    firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<InventoryItem>
) : InventoryItemRepository {
    private val tag = this::class.simpleName
    private val parties = firestore.collection(COLLECTION_PARTIES);

    override fun findAllForCharacter(characterId: CharacterId) =
        QueryLiveData(
            inventoryItems(characterId),
            mapper
        )

    override suspend fun save(characterId: CharacterId, item: InventoryItem) {
        val data = mapper.toDocumentData(item)
        Log.d(tag,"Saving inventory item $data for character $characterId")

        inventoryItems(characterId)
            .document(item.id.toString())
            .set(data, SetOptions.merge())
            .await()
    }

    override suspend fun get(characterId: CharacterId, itemId: InventoryItemId): InventoryItem {
        try {
            return mapper.fromDocumentSnapshot(
                inventoryItems(characterId)
                    .document(itemId.toString())
                    .get()
                    .await()
            )
        } catch (e: FirebaseFirestoreException) {
            throw InventoryItemNotFound(itemId, characterId, e)
        }
    }

    override suspend fun remove(characterId: CharacterId, itemId: InventoryItemId) {
        inventoryItems(characterId).document(itemId.toString()).delete().await()
    }

    private fun inventoryItems(characterId: CharacterId): CollectionReference {
        return parties.document(characterId.partyId.toString())
            .collection(COLLECTION_CHARACTERS)
            .document(characterId.userId)
            .collection(COLLECTION_INVENTORY_ITEMS)
    }
}