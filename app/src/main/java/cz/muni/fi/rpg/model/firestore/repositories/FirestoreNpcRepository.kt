package cz.muni.fi.rpg.model.firestore.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import cz.muni.fi.rpg.model.domain.encounter.*
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.model.firestore.*
import cz.muni.fi.rpg.model.firestore.AggregateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

internal class FirestoreNpcRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Npc>
) : NpcRepository {
    override fun findByEncounter(encounterId: EncounterId): Flow<List<Npc>> = queryFlow(
        npcs(encounterId).orderBy("position", Query.Direction.ASCENDING),
        mapper
    )

    override suspend fun get(id: NpcId): Npc {
        try {
            return mapper.fromDocumentSnapshot(
                npcs(id.encounterId)
                    .document(id.npcId.toString())
                    .get()
                    .await()
            )
        } catch (e: FirebaseFirestoreException) {
            throw CombatantNotFound(id, e)
        }
    }

    override suspend fun save(encounterId: EncounterId, vararg npcs: Npc) {
        val collection = npcs(encounterId)

        firestore.runTransaction { transaction ->
            npcs.forEach { npc ->
                transaction.set(
                    collection.document(npc.id.toString()),
                    mapper.toDocumentData(npc),
                    SetOptions.merge()
                )
            }
        }.await()
    }

    override suspend fun remove(id: NpcId) {
        npcs(id.encounterId).document(id.npcId.toString()).delete().await()
    }

    override suspend fun getNextPosition(encounterId: EncounterId): Int {
        val snapshot = npcs(encounterId)
            .orderBy("position", Query.Direction.DESCENDING)
            .get()
            .await()

        val lastPosition = snapshot.documents.map(mapper::fromDocumentSnapshot)
            .getOrNull(0)?.position ?: -1

        return lastPosition + 1
    }

    private fun npcs(encounterId: EncounterId) =
        firestore.collection(COLLECTION_PARTIES)
            .document(encounterId.partyId.toString())
            .collection(COLLECTION_ENCOUNTERS)
            .document(encounterId.encounterId.toString())
            .collection(COLLECTION_NPCS)
}