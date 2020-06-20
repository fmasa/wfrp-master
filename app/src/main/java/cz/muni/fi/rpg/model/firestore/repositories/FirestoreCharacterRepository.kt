package cz.muni.fi.rpg.model.firestore.repositories

import androidx.lifecycle.LiveData
import arrow.core.Either
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.firestore.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

internal class FirestoreCharacterRepository(
    firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Character>
) : CharacterRepository {
    private val parties = firestore.collection(COLLECTION_PARTIES)

    override suspend fun save(partyId: UUID, character: Character) {
        val data = mapper.toDocumentData(character)

        Timber.d("Saving character $data in party $partyId to firestore")
        characters(partyId).document(character.userId).set(data, SetOptions.merge()).await()
    }

    override suspend fun get(characterId: CharacterId): Character {
        try {
            return mapper.fromDocumentSnapshot(
                characters(characterId.partyId)
                    .document(characterId.userId)
                    .get()
                    .await()
            )
        } catch (e: FirebaseFirestoreException) {
            throw CharacterNotFound(characterId, e)
        }
    }

    override fun getLive(characterId: CharacterId): LiveData<Either<CharacterNotFound, Character>> {
        return DocumentLiveData(
            characters(
                characterId.partyId
            ).document(characterId.userId)
        ) {
            it.bimap({ e -> CharacterNotFound(characterId, e) }, mapper::fromDocumentSnapshot)
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