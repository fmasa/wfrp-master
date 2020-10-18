package cz.muni.fi.rpg.model.firestore.repositories

import com.google.firebase.firestore.FirebaseFirestore
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import cz.muni.fi.rpg.model.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.*

@ExperimentalCoroutinesApi
internal class FirestoreSkillRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Skill>
) : SkillRepository {
    override fun forCharacter(characterId: CharacterId): Flow<List<Skill>> =
        queryFlow(skillsCollection(characterId), mapper)

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
            .document(characterId.id)
            .collection(COLLECTION_SKILLS)
}