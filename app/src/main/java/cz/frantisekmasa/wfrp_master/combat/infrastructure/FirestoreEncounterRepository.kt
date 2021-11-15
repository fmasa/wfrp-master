package cz.frantisekmasa.wfrp_master.combat.infrastructure

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.EncounterNotFound
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.EncounterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firestore.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firestore.COLLECTION_PARTIES
import cz.frantisekmasa.wfrp_master.common.core.firestore.documentFlow
import cz.frantisekmasa.wfrp_master.common.core.firestore.queryFlow
import kotlinx.coroutines.tasks.await

/* internal */ class FirestoreEncounterRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Encounter>
) : EncounterRepository {
    private val parties = firestore.collection(COLLECTION_PARTIES)

    override suspend fun get(id: EncounterId): Encounter {
        try {
            return mapper.fromDocumentSnapshot(
                encounters(id.partyId)
                    .document(id.encounterId.toString())
                    .get()
                    .await()
            )
        } catch (e: FirebaseFirestoreException) {
            throw EncounterNotFound(id, e)
        }
    }

    override fun getLive(id: EncounterId) = documentFlow(
        encounters(id.partyId).document(id.encounterId.toString())
    ) {
        it.bimap(
            { e -> EncounterNotFound(id, e) },
            mapper::fromDocumentSnapshot
        )
    }

    override suspend fun save(partyId: PartyId, vararg encounters: Encounter) {
        firestore.runTransaction { transaction ->
            encounters.forEach { encounter ->
                transaction.set(
                    encounters(partyId).document(encounter.id.toString()),
                    mapper.toDocumentData(encounter),
                    SetOptions.merge()
                )
            }
        }.await()
    }

    override fun findByParty(partyId: PartyId) = queryFlow(
        encounters(partyId).orderBy("position", Query.Direction.ASCENDING),
        mapper
    )

    override suspend fun remove(id: EncounterId) {
        encounters(id.partyId).document(id.encounterId.toString()).delete().await()
    }

    override suspend fun getNextPosition(partyId: PartyId): Int {
        val snapshot = encounters(partyId)
            .orderBy("position", Query.Direction.DESCENDING)
            .get()
            .await()

        val lastPosition = snapshot.documents.map(mapper::fromDocumentSnapshot)
            .getOrNull(0)?.position ?: -1

        return lastPosition + 1
    }

    private fun encounters(partyId: PartyId) =
        parties.document(partyId.toString()).collection(COLLECTION_ENCOUNTERS)
}
