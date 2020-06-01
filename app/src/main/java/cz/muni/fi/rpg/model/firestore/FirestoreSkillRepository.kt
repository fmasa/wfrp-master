package cz.muni.fi.rpg.model.firestore

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import kotlinx.coroutines.tasks.await
import java.util.*

internal class FirestoreSkillRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Skill>
) : SkillRepository {
    override fun forCharacter(characterId: CharacterId): LiveData<List<Skill>> {
        return QueryLiveData(skillsCollection(characterId), mapper)
    }

    override suspend fun remove(characterId: CharacterId, skillId: UUID) {
        skillsCollection(characterId).document(skillId.toString()).delete().await()
    }

    override suspend fun save(characterId: CharacterId, skill: Skill) {
        skillsCollection(characterId)
            .document(skill.id.toString())
            .set(mapper.toDocumentData(skill))
    }

    private fun skillsCollection(characterId: CharacterId) =
        firestore.collection(COLLECTION_PARTIES)
            .document(characterId.partyId.toString())
            .collection(COLLECTION_CHARACTERS)
            .document(characterId.userId)
            .collection(COLLECTION_SKILLS)
}