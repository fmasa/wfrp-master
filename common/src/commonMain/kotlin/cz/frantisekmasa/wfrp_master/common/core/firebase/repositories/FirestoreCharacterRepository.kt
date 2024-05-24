package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import arrow.core.left
import arrow.core.right
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
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
import kotlinx.coroutines.flow.map

class FirestoreCharacterRepository(
    firestore: FirebaseFirestore,
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

    override suspend fun hasCharacterInParty(
        userId: String,
        partyId: PartyId,
    ): Boolean {
        return characters(partyId)
            .where("userId", equalTo = userId)
            .get()
            .documents
            .isNotEmpty()
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
}
