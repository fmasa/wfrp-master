package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.firebase.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.documents
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Npc
import cz.frantisekmasa.wfrp_master.common.encounters.domain.NpcNotFound
import cz.frantisekmasa.wfrp_master.common.encounters.domain.NpcRepository
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.FirestoreException
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Query
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow

/* internal */ class FirestoreNpcRepository(
    private val firestore: Firestore,
    private val mapper: AggregateMapper<Npc>
) : NpcRepository {
    override fun findByEncounter(encounterId: EncounterId): Flow<List<Npc>> =
        npcs(encounterId)
            .orderBy("position", Query.Direction.ASCENDING)
            .documents(mapper)

    override suspend fun get(id: NpcId): Npc {
        try {
            val data = npcs(id.encounterId).document(id.npcId.toString()).get().data
                ?: throw NpcNotFound(id)

            return mapper.fromDocumentData(data)
        } catch (e: FirestoreException) {
            throw NpcNotFound(id, e)
        }
    }

    override suspend fun save(encounterId: EncounterId, vararg npcs: Npc) {
        val collection = npcs(encounterId)

        firestore.runTransaction { transaction ->
            npcs.forEach { npc ->
                transaction.set(
                    collection.document(npc.id.toString()),
                    mapper.toDocumentData(npc),
                    SetOptions.MERGE
                )
            }
        }
    }

    override suspend fun remove(id: NpcId) {
        npcs(id.encounterId).document(id.npcId.toString()).delete()
    }

    override suspend fun getNextPosition(encounterId: EncounterId): Int {
        val snapshot = npcs(encounterId)
            .orderBy("position", Query.Direction.DESCENDING)
            .get()

        val lastPosition = snapshot.documents.map(mapper::fromDocumentSnapshot)
            .getOrNull(0)?.position ?: -1

        return lastPosition + 1
    }

    private fun npcs(encounterId: EncounterId) =
        firestore.collection(Schema.Parties)
            .document(encounterId.partyId.toString())
            .collection(Schema.Party.Encounters)
            .document(encounterId.encounterId.toString())
            .collection(Schema.Party.Encounter.Npcs)
}
