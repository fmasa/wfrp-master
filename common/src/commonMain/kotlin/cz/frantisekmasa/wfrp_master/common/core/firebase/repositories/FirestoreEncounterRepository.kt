package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import arrow.core.left
import arrow.core.right
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.firestore.setWithTopLevelFieldsMerge
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterNotFound
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterRepository
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.FirestoreExceptionCode
import dev.gitlive.firebase.firestore.orderBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestoreEncounterRepository(
    private val firestore: FirebaseFirestore,
) : EncounterRepository {
    private val parties = firestore.collection(Schema.PARTIES)

    override suspend fun get(id: EncounterId): Encounter {
        try {
            val snapshot = encounters(id.partyId).document(id.encounterId.toString()).get()

            if (!snapshot.exists) {
                throw EncounterNotFound(id)
            }

            return snapshot.data(Encounter.serializer())
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirestoreExceptionCode.UNAVAILABLE) {
                throw CouldNotConnectToBackend(e)
            }

            throw EncounterNotFound(id, e)
        }
    }

    override suspend fun update(
        id: EncounterId,
        mutator: (Encounter) -> Encounter,
    ) {
        val documentRef = encounters(id.partyId).document(id.encounterId.toString())

        firestore.runTransaction {
            val snapshot = get(documentRef)

            if (!snapshot.exists) {
                throw EncounterNotFound(id)
            }

            val encounter = snapshot.data(Encounter.serializer())
            val updatedEncounter = mutator(encounter)

            if (updatedEncounter == encounter) {
                return@runTransaction
            }

            setWithTopLevelFieldsMerge(
                documentRef = documentRef,
                data = updatedEncounter,
            )
        }
    }

    override fun getLive(id: EncounterId) =
        encounters(id.partyId)
            .document(id.encounterId.toString())
            .snapshots
            .map { snapshot ->
                if (snapshot.exists) {
                    snapshot.data(Encounter.serializer()).right()
                } else {
                    EncounterNotFound(id).left()
                }
            }

    override suspend fun save(
        partyId: PartyId,
        vararg encounters: Encounter,
    ) {
        firestore.runTransaction {
            encounters.forEach { encounter ->
                setWithTopLevelFieldsMerge(
                    documentRef = encounters(partyId).document(encounter.id.toString()),
                    data = encounter,
                )
            }
        }
    }

    override fun findByParty(partyId: PartyId): Flow<List<Encounter>> {
        return encounters(partyId)
            .orderBy("position")
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data(Encounter.serializer()) } }
    }

    override suspend fun remove(id: EncounterId) {
        encounters(id.partyId).document(id.encounterId.toString()).delete()
    }

    override suspend fun getNextPosition(partyId: PartyId): Int {
        val snapshot =
            encounters(partyId)
                .orderBy("position", Direction.DESCENDING)
                .get()

        val lastPosition =
            snapshot.documents
                .firstOrNull()
                ?.data(Encounter.serializer())
                ?.position ?: -1

        return lastPosition + 1
    }

    private fun encounters(partyId: PartyId) = parties.document(partyId.toString()).collection(Schema.Party.ENCOUNTERS)
}
