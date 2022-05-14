package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.firebase.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.documents
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow


open class FirestoreCharacterItemRepository<T : CharacterItem>(
    private val collectionName: String,
    protected val mapper: AggregateMapper<T>,
    private val firestore: Firestore,
) : CharacterItemRepository<T> {

    override fun findAllForCharacter(characterId: CharacterId): Flow<List<T>> =
        itemCollection(characterId)
            .orderBy("name")
            .documents(mapper)

    override suspend fun remove(characterId: CharacterId, itemId: Uuid) {
        itemCollection(characterId)
            .document(itemId.toString())
            .delete()
    }

    override suspend fun save(characterId: CharacterId, item: T) {
        val data = mapper.toDocumentData(item)

        itemCollection(characterId)
            .document(item.id.toString())
            .set(data, SetOptions.mergeFields(data.keys))
    }

    protected fun itemCollection(characterId: CharacterId) =
        firestore.collection(Schema.Parties)
            .document(characterId.partyId.toString())
            .collection(Schema.Characters)
            .document(characterId.id)
            .collection(collectionName)
}
