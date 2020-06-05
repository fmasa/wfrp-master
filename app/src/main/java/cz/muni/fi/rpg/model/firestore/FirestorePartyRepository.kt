package cz.muni.fi.rpg.model.firestore

import android.util.Log
import androidx.lifecycle.LiveData
import arrow.core.Either
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import kotlinx.coroutines.tasks.await
import java.util.*

internal class FirestorePartyRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Party>
) : PartyRepository {
    private val tag = this::class.simpleName
    private val parties = firestore.collection("parties");

    override suspend fun save(party: Party) {
        val data = mapper.toDocumentData(party)

        Log.d(tag,"Saving party $data to firestore")
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

    override suspend fun get(id: UUID): Party {
        try {
            val party = parties.document(id.toString()).get().await();
            return this.mapper.fromDocumentSnapshot(party);
        } catch (e: FirebaseFirestoreException) {
            throw PartyNotFound(id, e)
        }
    }

    override fun getLive(id: UUID): LiveData<Either<PartyNotFound, Party>> {
        return DocumentLiveData(parties.document(id.toString())) {
            it.bimap({ e -> PartyNotFound(id, e) }, { snapshot -> mapper.fromDocumentSnapshot(snapshot) })
        }
    }

    override fun forUser(userId: String): LiveData<List<Party>> =
        QueryLiveData(parties.whereArrayContains("users", userId), mapper)
}