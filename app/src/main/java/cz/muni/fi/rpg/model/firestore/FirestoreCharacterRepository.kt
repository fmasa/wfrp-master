package cz.muni.fi.rpg.model.firestore

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import cz.muni.fi.rpg.common.ViewHolder
import cz.muni.fi.rpg.common.ViewHolderFactory
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.infrastructure.GsonSnapshotParser
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirestoreCharacterRepository @Inject constructor(
    private val gson: Gson,
    firestore: FirebaseFirestore
) : CharacterRepository {
    private val parties = firestore.collection(COLLECTION_PARTIES)
    private val parser = GsonSnapshotParser(Character::class, gson)

    override suspend fun save(partyId: UUID, character: Character) {
        characters(partyId).document(character.userId).set(
            gson.fromJson(gson.toJson(character), Map::class.java),
            SetOptions.merge()
        ).await()
    }

    override suspend fun get(partyId: UUID, userId: String): Character {
        try {
            return parser.parseSnapshot(characters(partyId).document(userId).get().await())
        } catch (e: FirebaseFirestoreException) {
            throw CharacterNotFound(userId, partyId, e)
        }
    }

    override fun getLive(partyId: UUID, userId: String) =
        DocumentLiveData(characters(partyId).document(userId)) {
            it.bimap(
                { e -> CharacterNotFound(userId, partyId, e) },
                { snapshot -> parser.parseSnapshot(snapshot) }
            )
        }

    override suspend fun hasCharacterInParty(userId: String, partyId: UUID): Boolean {
        return characters(partyId).whereEqualTo("userId", userId).get().await().size() != 0
    }

    override fun inParty(
        partyId: UUID,
        lifecycleOwner: LifecycleOwner,
        viewHolderFactory: ViewHolderFactory<Character>
    ): RecyclerView.Adapter<ViewHolder<Character>> {
        val options = FirestoreRecyclerOptions.Builder<Character>()
            .setLifecycleOwner(lifecycleOwner)
            .setQuery(characters(partyId), parser)
            .build()

        return FirestoreRecyclerAdapter(
            options,
            viewHolderFactory
        )
    }

    private fun characters(partyId: UUID) =
        parties.document(partyId.toString()).collection(COLLECTION_CHARACTERS)
}