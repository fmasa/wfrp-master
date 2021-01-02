package cz.frantisekmasa.wfrp_master.core.firestore.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.firestore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

/* internal */ class FirestoreCharacterRepository(
    firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Character>
) : CharacterRepository {
    private val parties = firestore.collection(COLLECTION_PARTIES)

    override suspend fun save(partyId: UUID, character: Character) {
        val data = mapper.toDocumentData(character)

        Timber.d("Saving character $data in party $partyId to firestore")
        characters(partyId).document(character.id).set(data).await()
    }

    override suspend fun get(characterId: CharacterId): Character {
        try {
            val snapshot = characters(characterId.partyId)
                .document(characterId.id)
                .get()
                .await()

            if (snapshot.data == null) {
                throw CharacterNotFound(characterId)
            }

            return mapper.fromDocumentSnapshot(snapshot)
        } catch (e: FirebaseFirestoreException) {
            throw CharacterNotFound(characterId, e)
        }
    }

    override fun getLive(characterId: CharacterId) = documentFlow(
        characters(characterId.partyId).document(characterId.id)
    ) {
        it.bimap({ e -> CharacterNotFound(characterId, e) }, mapper::fromDocumentSnapshot)
    }

    override suspend fun hasCharacterInParty(userId: String, partyId: UUID): Boolean {
        return characters(partyId).whereEqualTo("userId", userId).get().await().size() != 0
    }

    override fun inParty(partyId: UUID): Flow<List<Character>> =
    // TODO: Filter archived characters via whereEqualTo() once all historic characters have `archived` field set
        // These should be migrated in 1.14
        queryFlow(
            characters(partyId),
            mapper
        ).map { parties -> parties.filter { !it.isArchived() } }

    private fun characters(partyId: UUID) =
        parties.document(partyId.toString()).collection(COLLECTION_CHARACTERS)
}