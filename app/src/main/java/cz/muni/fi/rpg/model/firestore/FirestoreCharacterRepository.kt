package cz.muni.fi.rpg.model.firestore

import android.util.Log
import androidx.lifecycle.LiveData
import arrow.core.Either
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

internal class FirestoreCharacterRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Character>
) : CharacterRepository {
    private val tag = this::class.simpleName
    private val parties = firestore.collection(COLLECTION_PARTIES)

    override suspend fun save(partyId: UUID, character: Character) {
        val data = mapper.toDocumentData(character)

        Log.d(tag,"Saving character $data in party $partyId to firestore")
        characters(partyId).document(character.userId).set(data, SetOptions.merge()).await()
    }

    override suspend fun get(partyId: UUID, userId: String): Character {
        try {
            return mapper.fromDocumentSnapshot(characters(partyId).document(userId).get().await())
        } catch (e: FirebaseFirestoreException) {
            throw CharacterNotFound(userId, partyId, e)
        }
    }

    override fun getLive(
        partyId: UUID,
        userId: String
    ): LiveData<Either<CharacterNotFound, Character>> {
        return DocumentLiveData(characters(partyId).document(userId)) {
            it.bimap({ e -> CharacterNotFound(userId, partyId, e) }, mapper::fromDocumentSnapshot)
        }
    }

    override suspend fun hasCharacterInParty(userId: String, partyId: UUID): Boolean {
        return characters(partyId).whereEqualTo("userId", userId).get().await().size() != 0
    }

    override fun inParty(partyId: UUID): LiveData<List<Character>> =
        QueryLiveData(characters(partyId), mapper)

    private fun characters(partyId: UUID) =
        parties.document(partyId.toString()).collection(COLLECTION_CHARACTERS)
}