package cz.muni.fi.rpg.model.firestore

import androidx.lifecycle.LiveData
import arrow.core.Either
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.domain.party.PartyNotFound
import cz.muni.fi.rpg.model.domain.party.PartyRepository
import cz.muni.fi.rpg.model.infrastructure.GsonSnapshotParser
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirestorePartyRepository @Inject constructor(
    private val gson: Gson,
    firestore: FirebaseFirestore
) : PartyRepository {
    private val parties = firestore.collection("parties");
    private val parser = GsonSnapshotParser(Party::class, gson);

    override suspend fun save(party: Party) {
        parties.document(party.id.toString()).set(
            gson.fromJson(gson.toJson(party), Map::class.java),
            SetOptions.merge()
        ).await();
    }

    override suspend fun get(id: UUID): Party {
        try {
            val party = parties.document(id.toString()).get().await();
            return this.parser.parseSnapshot(party);
        } catch (e: FirebaseFirestoreException) {
            throw PartyNotFound(id, e)
        }
    }

    override fun getLive(id: UUID): LiveData<Either<PartyNotFound, Party>> {
        return DocumentLiveData(parties.document(id.toString())) {
            it.bimap({ e -> PartyNotFound(id, e) }, { snapshot -> parser.parseSnapshot(snapshot) })
        }
    }

    override fun forUser(userId: String): LiveData<List<Party>> =
        QueryLiveData(parties.whereArrayContains("users", userId), parser)
}