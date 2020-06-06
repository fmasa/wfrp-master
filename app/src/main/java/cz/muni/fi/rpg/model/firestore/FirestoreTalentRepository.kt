package cz.muni.fi.rpg.model.firestore

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.domain.talents.TalentRepository
import kotlinx.coroutines.tasks.await
import java.util.*

internal class FirestoreTalentRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Talent>
) : TalentRepository {
    override fun findAllForCharacter(characterId: CharacterId): LiveData<List<Talent>> {
        return QueryLiveData(talentsCollection(characterId), mapper)
    }

    override suspend fun remove(characterId: CharacterId, talentId: UUID) {
        talentsCollection(characterId).document(talentId.toString()).delete().await()
    }

    override suspend fun save(characterId: CharacterId, talent: Talent) {
        talentsCollection(characterId)
            .document(talent.id.toString())
            .set(mapper.toDocumentData(talent))
    }

    private fun talentsCollection(characterId: CharacterId) =
        firestore.collection(COLLECTION_PARTIES)
            .document(characterId.partyId.toString())
            .collection(COLLECTION_CHARACTERS)
            .document(characterId.userId)
            .collection(COLLECTION_TALENTS)
}