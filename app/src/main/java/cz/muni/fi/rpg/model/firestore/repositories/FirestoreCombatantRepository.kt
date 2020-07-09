package cz.muni.fi.rpg.model.firestore.repositories

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import cz.muni.fi.rpg.model.domain.encounter.*
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.model.firestore.*
import cz.muni.fi.rpg.model.firestore.AggregateMapper
import cz.muni.fi.rpg.model.firestore.QueryLiveData
import kotlinx.coroutines.tasks.await
import java.util.*

internal class FirestoreCombatantRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Combatant>
) : CombatantRepository {
    override fun findByEncounter(encounterId: EncounterId): LiveData<List<Combatant>> {
        return QueryLiveData(
            combatants(encounterId).orderBy("position", Query.Direction.ASCENDING),
            mapper
        )
    }

    override suspend fun get(id: CombatantId): Combatant {
        try {
            return mapper.fromDocumentSnapshot(
                combatants(id.encounterId)
                    .document(id.combatantId.toString())
                    .get()
                    .await()
            )
        } catch (e: FirebaseFirestoreException) {
            throw CombatantNotFound(id, e)
        }
    }

    override suspend fun save(encounterId: EncounterId, vararg combatants: Combatant) {
        val collection = combatants(encounterId)

        firestore.runTransaction { transaction ->
            combatants.forEach { combatant ->
                transaction.set(
                    collection.document(combatant.id.toString()),
                    mapper.toDocumentData(combatant),
                    SetOptions.merge()
                )
            }
        }.await()
    }

    override suspend fun remove(id: CombatantId) {
        combatants(id.encounterId).document(id.combatantId.toString()).delete().await()
    }

    override suspend fun getNextPosition(encounterId: EncounterId): Int {
        val snapshot = combatants(encounterId)
            .orderBy("position", Query.Direction.DESCENDING)
            .get()
            .await()

        val lastPosition = snapshot.documents.map(mapper::fromDocumentSnapshot)
            .getOrNull(0)?.position ?: -1

        return lastPosition + 1
    }

    private fun combatants(encounterId: EncounterId) =
        firestore.collection(COLLECTION_PARTIES)
            .document(encounterId.partyId.toString())
            .collection(COLLECTION_ENCOUNTERS)
            .document(encounterId.encounterId.toString())
            .collection(COLLECTION_COMBATANTS)
}