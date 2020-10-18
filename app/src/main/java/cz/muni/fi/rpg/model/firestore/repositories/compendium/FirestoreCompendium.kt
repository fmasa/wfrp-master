package cz.muni.fi.rpg.model.firestore.repositories.compendium

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import cz.muni.fi.rpg.model.domain.compendium.Compendium
import cz.muni.fi.rpg.model.domain.compendium.CompendiumItem
import cz.muni.fi.rpg.model.firestore.AggregateMapper
import cz.muni.fi.rpg.model.firestore.COLLECTION_PARTIES
import cz.muni.fi.rpg.model.firestore.queryFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

@ExperimentalCoroutinesApi
internal class FirestoreCompendium<T : CompendiumItem>(
    private val collectionName: String,
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<T>,
) : Compendium<T> {

    override fun liveForParty(partyId: UUID): Flow<List<T>> = queryFlow(collection(partyId), mapper)

    override suspend fun saveItem(partyId: UUID, item: T) {
        val data = mapper.toDocumentData(item)
        Timber.d("Saving Compendium item $data to $collectionName compendium of party $partyId")

        collection(partyId)
            .document(item.id.toString())
            .set(data, SetOptions.merge())
            .await()
    }

    override suspend fun remove(partyId: UUID, item: T) {
        collection(partyId)
            .document(item.id.toString())
            .delete()
            .await()
    }

    private fun collection(partyId: UUID) =
        firestore.collection(COLLECTION_PARTIES)
            .document(partyId.toString())
            .collection(collectionName)
}