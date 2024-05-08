package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import arrow.core.left
import arrow.core.right
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.FirestoreExceptionCode
import dev.gitlive.firebase.firestore.QuerySnapshot
import dev.gitlive.firebase.firestore.Transaction
import dev.gitlive.firebase.firestore.where
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map

class FirestorePartyRepository(
    private val firestore: FirebaseFirestore,
) : PartyRepository {
    private val parties = firestore.collection(Schema.PARTIES)

    override suspend fun save(party: Party) {
        Napier.d("Saving party $party to firestore")

        try {
            firestore.runTransaction {
                set(
                    documentRef = parties.document(party.id.toString()),
                    strategy = Party.serializer(),
                    data = party,
                    merge = true,
                    encodeDefaults = true,
                )
            }
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirestoreExceptionCode.UNAVAILABLE) {
                throw CouldNotConnectToBackend(e)
            }

            throw e
        }
    }

    override fun save(
        transaction: Transaction,
        party: Party,
    ) {
        Napier.d("Saving party $party to firestore")

        transaction.set(
            documentRef = parties.document(party.id.toString()),
            strategy = Party.serializer(),
            data = party,
            merge = true,
            encodeDefaults = true,
        )
    }

    override suspend fun update(
        id: PartyId,
        mutator: (Party) -> Party,
    ) {
        firestore.runTransaction {
            val party = get(this, id)
            val updatedParty = mutator(party)

            if (updatedParty != party) {
                save(this, updatedParty)
            }
        }
    }

    override suspend fun get(
        transaction: Transaction,
        id: PartyId,
    ): Party {
        val snapshot = transaction.get(parties.document(id.toString()))

        if (!snapshot.exists) {
            throw PartyNotFound(id)
        }

        return snapshot.data(Party.serializer())
    }

    override fun getLive(id: PartyId) =
        parties.document(id.toString())
            .snapshots
            .map { snapshot ->
                if (snapshot.exists) {
                    snapshot.data(Party.serializer()).right()
                } else {
                    PartyNotFound(id).left()
                }
            }

    override fun forUserLive(userId: UserId) =
        queryForUser(userId)
            .snapshots
            .map { it.toPartyList() }

    override suspend fun forUser(userId: UserId) =
        queryForUser(userId)
            .get()
            .toPartyList()

    private fun QuerySnapshot.toPartyList(): List<Party> {
        return documents.map { it.data(Party.serializer()) }
    }

    private fun queryForUser(userId: UserId) =
        parties
            .where("users", arrayContains = userId.toString())
            .where("archived", equalTo = false)
}
