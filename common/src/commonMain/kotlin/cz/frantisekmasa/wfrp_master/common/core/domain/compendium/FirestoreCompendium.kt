package cz.frantisekmasa.wfrp_master.common.core.domain.compendium

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.firestore.setWithTopLevelFieldsMerge
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.Transaction
import dev.gitlive.firebase.firestore.orderBy
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer

class FirestoreCompendium<T : CompendiumItem<T>>(
    private val collectionName: String,
    private val firestore: FirebaseFirestore,
    private val serializer: KSerializer<T>,
) : Compendium<T>, CoroutineScope by CoroutineScope(Dispatchers.IO) {
    override fun liveForParty(partyId: PartyId): Flow<List<T>> =
        collection(partyId)
            .orderBy("name")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(serializer) }
            }

    override suspend fun getItem(
        partyId: PartyId,
        itemId: Uuid,
    ): T {
        try {
            val snapshot = collection(partyId).document(itemId.toString()).get()

            if (!snapshot.exists) {
                throw CompendiumItemNotFound(
                    "Compendium item $itemId was not found in collection $collectionName",
                )
            }

            return snapshot.data(serializer)
        } catch (e: FirebaseFirestoreException) {
            throw CompendiumItemNotFound(
                "Compendium item $itemId was not found in collection $collectionName",
                e,
            )
        }
    }

    override fun getLive(
        partyId: PartyId,
        itemId: Uuid,
    ): Flow<Either<CompendiumItemNotFound, T>> {
        return collection(partyId).document(itemId.toString())
            .snapshots
            .map {
                if (it.exists) {
                    it.data(serializer).right()
                } else {
                    CompendiumItemNotFound("Compendium item $itemId was not found").left()
                }
            }
    }

    override suspend fun saveItems(
        partyId: PartyId,
        items: List<T>,
    ) {
        items.chunked(MAX_BATCH_SIZE).forEach { chunk ->
            firestore.runTransaction {
                chunk.forEach { item ->
                    Napier.d("Saving Compendium item $item to $collectionName compendium of party $partyId")

                    setWithTopLevelFieldsMerge(
                        documentRef = collection(partyId).document(item.id.toString()),
                        strategy = serializer,
                        data = item,
                    )
                }
            }
        }
    }

    override fun save(
        transaction: Transaction,
        partyId: PartyId,
        item: T,
    ) {
        Napier.d("Saving compendium item $item")
        transaction.setWithTopLevelFieldsMerge(
            documentRef = collection(partyId).document(item.id.toString()),
            strategy = serializer,
            data = item,
        )
    }

    override suspend fun remove(
        transaction: Transaction,
        partyId: PartyId,
        item: T,
    ) {
        transaction.delete(
            collection(partyId).document(item.id.toString()),
        )
    }

    private fun collection(partyId: PartyId) =
        firestore.collection(Schema.PARTIES)
            .document(partyId.toString())
            .collection(collectionName)

    companion object {
        private val MAX_BATCH_SIZE = 500
    }
}
