package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import arrow.core.left
import arrow.core.right
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.LocalCharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.firestore.setWithTopLevelFieldsMerge
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.Transaction
import dev.gitlive.firebase.firestore.orderBy
import dev.gitlive.firebase.firestore.where
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FirestoreCharacterRepository(
    private val firestore: FirebaseFirestore,
) : CharacterRepository {
    private val parties = firestore.collection(Schema.PARTIES)

    override suspend fun save(
        partyId: PartyId,
        character: Character,
    ) {
        Napier.d("Saving character $character in party $partyId to firestore")
        characters(partyId)
            .document(character.id)
            .setWithTopLevelFieldsMerge(
                strategy = Character.serializer(),
                data = character,
            )
    }

    override fun save(
        transaction: Transaction,
        partyId: PartyId,
        character: Character,
    ) {
        Napier.d("Saving character $character in party $partyId to firestore")

        transaction.setWithTopLevelFieldsMerge(
            documentRef = characters(partyId).document(character.id),
            strategy = Character.serializer(),
            data = character,
        )
    }

    override suspend fun get(characterId: CharacterId): Character {
        try {
            val snapshot =
                characters(characterId.partyId)
                    .document(characterId.id)
                    .get()

            if (!snapshot.exists) {
                throw CharacterNotFound(characterId)
            }

            return snapshot.data(Character.serializer())
        } catch (e: FirebaseFirestoreException) {
            throw CharacterNotFound(characterId, e)
        }
    }

    override fun findByIds(
        partyId: PartyId,
        characterIds: Set<LocalCharacterId>,
    ): Flow<Map<LocalCharacterId, Character>> {
        val collection = characters(partyId)

        if (characterIds.isEmpty()) {
            return flowOf(emptyMap())
        }

        return characterIds.chunked(FIRESTORE_MAX_IN_ITEMS)
            .map { chunk ->
                collection
                    .where { ("id" inArray chunk) and ("archived" equalTo false) }
                    .snapshots
                    .map { snapshot ->
                        snapshot.documents.map {
                            val character = it.data(Character.serializer())

                            character.id to character
                        }
                    }
            }.let { combine(it) { chunks -> chunks.asSequence().flatten().toMap() } }
    }

    override fun getLive(characterId: CharacterId) =
        characters(characterId.partyId)
            .document(characterId.id)
            .snapshots
            .map {
                if (it.exists) {
                    it.data(Character.serializer()).right()
                } else {
                    CharacterNotFound(characterId).left()
                }
            }

    override fun getPlayerCharactersInAllPartiesLive(userId: UserId): Flow<List<Pair<PartyId, Character>>> {
        return firestore.collectionGroup(Schema.CHARACTERS)
            .where { ("userId" equalTo userId.toString()) and ("archived" equalTo false) }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map {
                    PartyId(uuidFrom(it.reference.parent.parent!!.id)) to
                        it.data(Character.serializer())
                }
            }
    }

    override suspend fun findByCompendiumCareer(
        partyId: PartyId,
        careerId: Uuid,
    ): List<Character> {
        return characters(partyId)
            .where("compendiumCareer.careerId", equalTo = careerId.toString())
            .get()
            .documents
            .map { it.data(Character.serializer()) }
    }

    override fun inParty(
        partyId: PartyId,
        types: Set<CharacterType>,
    ): Flow<List<Character>> {
        return characters(partyId)
            .where { "archived" equalTo false }
            .where { "type" inArray types.map { it.name } }
            .orderBy("name")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data(Character.serializer()) }
            }
    }

    private fun characters(partyId: PartyId) =
        parties.document(partyId.toString())
            .collection(Schema.CHARACTERS)

    companion object {
        private const val FIRESTORE_MAX_IN_ITEMS = 30
    }
}
