package cz.muni.fi.rpg.model.firestore.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounter.EncounterNotFound
import cz.muni.fi.rpg.model.domain.encounter.EncounterRepository
import cz.muni.fi.rpg.model.firestore.*
import cz.muni.fi.rpg.model.firestore.AggregateMapper
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import java.util.*

@ExperimentalCoroutinesApi
internal class FirestoreEncounterRepository(
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

    override suspend fun save(partyId: UUID, vararg encounters: Encounter) {
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

    override fun findByParty(partyId: UUID) = queryFlow(
        encounters(partyId).orderBy("position", Query.Direction.ASCENDING),
        mapper
    )

    override suspend fun remove(id: EncounterId) {
        encounters(id.partyId).document(id.encounterId.toString()).delete().await()
    }

    override suspend fun getNextPosition(partyId: UUID): Int {
        val snapshot = encounters(partyId)
            .orderBy("position", Query.Direction.DESCENDING)
            .get()
            .await()

        val lastPosition = snapshot.documents.map(mapper::fromDocumentSnapshot)
            .getOrNull(0)?.position ?: -1

        return lastPosition + 1
    }

    private fun encounters(partyId: UUID) =
        parties.document(partyId.toString()).collection(COLLECTION_ENCOUNTERS)
}