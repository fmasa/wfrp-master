package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import arrow.core.left
import arrow.core.right
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.firebase.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firebase.documents
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.FirestoreException
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.SetOptions
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Source
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map


class FirestorePartyRepository(
    private val firestore: Firestore,
    private val mapper: AggregateMapper<Party>
) : PartyRepository {
    private val parties = firestore.collection("parties")

    override suspend fun save(party: Party) {
        val data = mapper.toDocumentData(party)

        Napier.d("Saving party $data to firestore")
        try {
            firestore.runTransaction { transaction ->
                transaction.set(
                    parties.document(party.id.toString()),
                    data,
                    SetOptions.mergeFields(data.keys),
                )
            }
        } catch (e: FirestoreException) {
            if (e.isUnavailable) {
                throw CouldNotConnectToBackend(e)
            }

            throw e
        }
    }

    override suspend fun update(id: PartyId, mutator: (Party) -> Party) {
        val party = get(id)
        val updatedParty = mutator(party)

        if (updatedParty != party) {
            save(updatedParty)
        }
    }

    override suspend fun get(id: PartyId): Party {
        try {
            return this.mapper.fromDocumentSnapshot(
                parties.document(id.toString()).get(Source.SERVER)
            )
        } catch (e: FirestoreException) {
            throw PartyNotFound(id, e)
        }
    }

    override fun getLive(id: PartyId) =
        parties.document(id.toString())
            .snapshots
            .map { snapshot ->
                snapshot.fold(
                    {
                        when (val data = it.data) {
                            null -> PartyNotFound(id).left()
                            else -> mapper.fromDocumentData(data).right()
                        }
                    },
                    { PartyNotFound(id, it).left() }
                )
            }


    override fun forUserLive(userId: String) = queryForUser(userId).documents(mapper)

    override suspend fun forUser(userId: String) =
        queryForUser(userId)
            .get()
            .documents
            .map { mapper.fromDocumentSnapshot(it) }

    private fun queryForUser(userId: String) = parties
        .whereArrayContains("users", userId)
        .whereEqualTo("archived", false)
}
