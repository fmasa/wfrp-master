package cz.frantisekmasa.wfrp_master.core.firestore.repositories

import com.google.firebase.firestore.FirebaseFirestore
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.firestore.AggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.COLLECTION_CHARACTERS
import cz.frantisekmasa.wfrp_master.core.firestore.COLLECTION_PARTIES
import cz.frantisekmasa.wfrp_master.core.firestore.queryFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

open class FirestoreCharacterItemRepository<T : CharacterItem>(
    private val collectionName: String,
    protected val mapper: AggregateMapper<T>,
    private val firestore: FirebaseFirestore,
) : CharacterItemRepository<T> {

    override fun findAllForCharacter(characterId: CharacterId): Flow<List<T>> =
        queryFlow(itemCollection(characterId).orderBy("name"), mapper)

    override suspend fun remove(characterId: CharacterId, itemId: UUID) {
        itemCollection(characterId).document(itemId.toString()).delete().await()
    }

    override suspend fun save(characterId: CharacterId, item: T) {
        itemCollection(characterId)
            .document(item.id.toString())
            .set(mapper.toDocumentData(item))
    }

    protected fun itemCollection(characterId: CharacterId) =
        firestore.collection(COLLECTION_PARTIES)
            .document(characterId.partyId.toString())
            .collection(COLLECTION_CHARACTERS)
            .document(characterId.id)
            .collection(collectionName)
}
