package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItemRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.firestore.setWithTopLevelFieldsMerge
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Transaction
import dev.gitlive.firebase.firestore.orderBy
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer

open class FirestoreCharacterItemRepository<T : CharacterItem<T, *>>(
    private val collectionName: String,
    private val firestore: FirebaseFirestore,
    private val serializer: KSerializer<T>,
) : CharacterItemRepository<T> {
    override fun findAllForCharacter(characterId: CharacterId): Flow<List<T>> =
        itemCollection(characterId)
            .orderBy("name")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(serializer) }
            }

    override fun getLive(
        characterId: CharacterId,
        itemId: Uuid,
    ): Flow<Either<CompendiumItemNotFound, T>> {
        return itemCollection(characterId)
            .document(itemId.toString())
            .snapshots
            .map {
                if (it.exists) {
                    it.data(serializer).right()
                } else {
                    CompendiumItemNotFound(null).left()
                }
            }
    }

    override suspend fun remove(
        characterId: CharacterId,
        itemId: Uuid,
    ) {
        itemCollection(characterId)
            .document(itemId.toString())
            .delete()
    }

    override fun remove(
        transaction: Transaction,
        characterId: CharacterId,
        itemId: Uuid,
    ) {
        transaction.delete(itemCollection(characterId).document(itemId.toString()))
    }

    override suspend fun save(
        characterId: CharacterId,
        item: T,
    ) {
        itemCollection(characterId)
            .document(item.id.toString())
            .setWithTopLevelFieldsMerge(
                data = item,
                strategy = serializer,
            )
    }

    override fun save(
        transaction: Transaction,
        characterId: CharacterId,
        item: T,
    ) {
        transaction.setWithTopLevelFieldsMerge(
            itemCollection(characterId).document(item.id.toString()),
            data = item,
            strategy = serializer,
        )
    }

    override suspend fun findByCompendiumId(
        partyId: PartyId,
        compendiumItemId: Uuid,
    ): List<Pair<CharacterId, T>> {
        return coroutineScope {
            firestore.collection(Schema.PARTIES)
                .document(partyId.toString())
                .collection(Schema.CHARACTERS)
                .where("archived", equalTo = false)
                .get()
                .documents
                .map { character ->
                    async(Dispatchers.IO) {
                        val characterId = CharacterId(partyId, character.id)

                        itemCollection(characterId)
                            .where("compendiumId", equalTo = compendiumItemId.toString())
                            .get()
                            .documents
                            .map { it.data(serializer) }
                            .map { characterId to it }
                    }
                }.awaitAll()
                .flatten()
        }
    }

    protected fun itemCollection(characterId: CharacterId) =
        firestore.collection(Schema.PARTIES)
            .document(characterId.partyId.toString())
            .collection(Schema.CHARACTERS)
            .document(characterId.id)
            .collection(collectionName)
}
