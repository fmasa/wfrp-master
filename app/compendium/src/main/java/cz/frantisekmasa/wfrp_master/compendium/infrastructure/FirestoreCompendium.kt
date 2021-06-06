package cz.frantisekmasa.wfrp_master.compendium.infrastructure

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import cz.frantisekmasa.wfrp_master.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.firestore.AggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.COLLECTION_PARTIES
import cz.frantisekmasa.wfrp_master.core.firestore.queryFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

/* internal */ class FirestoreCompendium<T : CompendiumItem>(
    private val collectionName: String,
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<T>,
) : Compendium<T>, CoroutineScope by CoroutineScope(Dispatchers.IO) {

    override fun liveForParty(partyId: PartyId): Flow<List<T>> = queryFlow(
        collection(partyId).orderBy("name"),
        mapper,
    )

    override suspend fun getItem(partyId: PartyId, itemId: UUID): T {
        try {
            val snapshot = collection(partyId).document(itemId.toString()).get().await()

            if (snapshot.data == null) {
                throw CompendiumItemNotFound(
                    "Compendium item $itemId was not found in collection $collectionName"
                )
            }

            return mapper.fromDocumentSnapshot(snapshot)
        } catch (e: FirebaseFirestoreException) {
            throw CompendiumItemNotFound(
                "Compendium item $itemId was not found in collection $collectionName",
                e
            )
        }
    }

    override suspend fun saveItems(partyId: PartyId, vararg items: T) {
        val itemsData = coroutineScope {
            items.map { it.id to async { mapper.toDocumentData(it) } }
                .map { (id, data) -> id to data.await() }
        }

        firestore.runTransaction { transaction ->
            itemsData.forEach { (id, data) ->
                Timber.d("Saving Compendium item $data to $collectionName compendium of party $partyId")

                transaction.set(
                    collection(partyId).document(id.toString()),
                    data,
                    SetOptions.merge()
                )
            }
        }.await()
    }

    override suspend fun remove(partyId: PartyId, item: T) {
        collection(partyId)
            .document(item.id.toString())
            .delete()
            .await()
    }

    private fun collection(partyId: PartyId) =
        firestore.collection(COLLECTION_PARTIES)
            .document(partyId.toString())
            .collection(collectionName)
}