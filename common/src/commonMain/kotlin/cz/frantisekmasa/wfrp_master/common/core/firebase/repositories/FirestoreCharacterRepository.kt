package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import arrow.core.left
import arrow.core.right
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.firebase.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.core.firebase.documents
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.FirestoreException
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.SetOptions
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class FirestoreCharacterRepository(
    firestore: Firestore,
    private val mapper: AggregateMapper<Character>
) : CharacterRepository {
    private val parties = firestore.collection(Schema.Parties)

    override suspend fun save(partyId: PartyId, character: Character) {
        val data = mapper.toDocumentData(character)

        Napier.d("Saving character $data in party $partyId to firestore")
        characters(partyId)
            .document(character.id)
            .set(data, SetOptions.MERGE)
    }

    override suspend fun get(characterId: CharacterId): Character {
        try {
            val snapshot = characters(characterId.partyId)
                .document(characterId.id)
                .get()

            if (snapshot.data == null) {
                throw CharacterNotFound(characterId)
            }

            return mapper.fromDocumentSnapshot(snapshot)
        } catch (e: FirestoreException) {
            throw CharacterNotFound(characterId, e)
        }
    }

    override fun getLive(characterId: CharacterId) =
        characters(characterId.partyId)
            .document(characterId.id)
            .snapshots
            .map { snapshot ->
                snapshot.fold(
                    { mapper.fromDocumentSnapshot(it).right() },
                    { CharacterNotFound(characterId, it).left() },
                )
            }

    override suspend fun hasCharacterInParty(userId: String, partyId: PartyId): Boolean {
        return characters(partyId)
            .whereEqualTo("userId", userId)
            .get()
            .documents
            .isNotEmpty()
    }

    override fun inParty(partyId: PartyId): Flow<List<Character>> =
    // TODO: Filter archived characters via whereEqualTo() once all historic characters have `archived` field set
        // These should be migrated in 1.14
        characters(partyId)
            .documents(mapper)
            .map { parties -> parties.filter { !it.isArchived } }

    private fun characters(partyId: PartyId) =
        parties.document(partyId.toString())
            .collection(Schema.Characters)
}
