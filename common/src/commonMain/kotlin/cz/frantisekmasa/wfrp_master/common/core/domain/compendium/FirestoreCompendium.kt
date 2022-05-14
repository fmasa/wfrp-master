package cz.frantisekmasa.wfrp_master.common.core.domain.compendium

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firebase.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.documents
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.FirestoreException
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.SetOptions
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow


class FirestoreCompendium<T : CompendiumItem<T>>(
    private val collectionName: String,
    private val firestore: Firestore,
    private val mapper: AggregateMapper<T>,
) : Compendium<T>, CoroutineScope by CoroutineScope(Dispatchers.IO) {

    override fun liveForParty(partyId: PartyId): Flow<List<T>> =
        collection(partyId)
            .orderBy("name")
            .documents(mapper)

    override suspend fun getItem(partyId: PartyId, itemId: Uuid): T {
        try {
            val snapshot = collection(partyId).document(itemId.toString()).get()

            return snapshot.data?.let(mapper::fromDocumentData)
                ?: throw CompendiumItemNotFound(
                    "Compendium item $itemId was not found in collection $collectionName"
                )
        } catch (e: FirestoreException) {
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
                Napier.d("Saving Compendium item $data to $collectionName compendium of party $partyId")

                transaction.set(
                    collection(partyId).document(id.toString()),
                    data,
                    SetOptions.mergeFields(data.keys)
                )
            }
        }
    }

    override suspend fun remove(partyId: PartyId, item: T) {
        collection(partyId)
            .document(item.id.toString())
            .delete()
    }

    private fun collection(partyId: PartyId) =
        firestore.collection(Schema.Parties)
            .document(partyId.toString())
            .collection(collectionName)
}
