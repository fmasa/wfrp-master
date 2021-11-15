package cz.frantisekmasa.wfrp_master.common.core.firestore.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.firestore.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firestore.documentFlow
import cz.frantisekmasa.wfrp_master.common.core.firestore.queryFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.tasks.await

/* internal */ class FirestorePartyRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Party>
) : PartyRepository {
    private val parties = firestore.collection("parties")

    override suspend fun save(party: Party) {
        val data = mapper.toDocumentData(party)

        Napier.d("Saving party $data to firestore")
        try {
            firestore.runTransaction { transaction ->
                transaction.set(parties.document(party.id.toString()), data, SetOptions.merge())
                null
            }.await()
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                throw CouldNotConnectToBackend(e)
            }

            throw e
        }
    }

    override suspend fun get(id: PartyId): Party {
        try {
            val party = parties.document(id.toString()).get(Source.SERVER).await()
            return this.mapper.fromDocumentSnapshot(party)
        } catch (e: FirebaseFirestoreException) {
            throw PartyNotFound(id, e)
        }
    }

    override fun getLive(id: PartyId) = documentFlow(parties.document(id.toString())) {
        it.bimap(
            { e -> PartyNotFound(id, e) },
            { snapshot -> mapper.fromDocumentSnapshot(snapshot) }
        )
    }

    override fun forUserLive(userId: String) = queryFlow(queryForUser(userId), mapper)

    override suspend fun forUser(userId: String) =
        queryForUser(userId)
            .get()
            .await()
            .documents
            .map { mapper.fromDocumentSnapshot(it) }

    private fun queryForUser(userId: String) = parties
        .whereArrayContains("users", userId)
        .whereEqualTo("archived", false)
}
