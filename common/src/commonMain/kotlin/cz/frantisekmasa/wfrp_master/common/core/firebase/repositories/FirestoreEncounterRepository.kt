package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import arrow.core.left
import arrow.core.right
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firebase.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.documents
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterNotFound
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterRepository
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.FirestoreException
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Query
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestoreEncounterRepository(
    private val firestore: Firestore,
    private val mapper: AggregateMapper<Encounter>
) : EncounterRepository {
    private val parties = firestore.collection(Schema.Parties)

    override suspend fun get(id: EncounterId): Encounter {
        try {
            val data = encounters(id.partyId).document(id.encounterId.toString()).get().data
                ?: throw EncounterNotFound(id)

            return mapper.fromDocumentData(data)
        } catch (e: FirestoreException) {
            if (e.isUnavailable) {
                throw CouldNotConnectToBackend(e)
            }

            throw EncounterNotFound(id, e)
        }
    }

    override fun getLive(id: EncounterId) =
        encounters(id.partyId)
            .document(id.encounterId.toString())
            .snapshots
            .map { snapshot ->
                snapshot.fold(
                    {
                        when (val data = it.data) {
                            null -> EncounterNotFound(id).left()
                            else -> mapper.fromDocumentData(data).right()
                        }
                    },
                    {
                        EncounterNotFound(id, it).left()
                    }
                )
            }

    override suspend fun save(partyId: PartyId, vararg encounters: Encounter) {
        firestore.runTransaction { transaction ->
            encounters.forEach { encounter ->
                val data = mapper.toDocumentData(encounter)

                transaction.set(
                    encounters(partyId).document(encounter.id.toString()),
                    data,
                    SetOptions.mergeFields(data.keys),
                )
            }
        }
    }

    override fun findByParty(partyId: PartyId): Flow<List<Encounter>> {
        return encounters(partyId)
            .orderBy("position", Query.Direction.ASCENDING)
            .documents(mapper)
    }

    override suspend fun remove(id: EncounterId) {
        encounters(id.partyId).document(id.encounterId.toString()).delete()
    }

    override suspend fun getNextPosition(partyId: PartyId): Int {
        val snapshot = encounters(partyId)
            .orderBy("position", Query.Direction.DESCENDING)
            .get()

        val lastPosition = snapshot.documents.map(mapper::fromDocumentSnapshot)
            .getOrNull(0)?.position ?: -1

        return lastPosition + 1
    }

    private fun encounters(partyId: PartyId) =
        parties.document(partyId.toString()).collection(Schema.Party.Encounters)
}
