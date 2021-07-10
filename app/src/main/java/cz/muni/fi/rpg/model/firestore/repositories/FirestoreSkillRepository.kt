package cz.muni.fi.rpg.model.firestore.repositories

import com.google.firebase.firestore.FirebaseFirestore
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.firestore.AggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.repositories.FirestoreCharacterItemRepository
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillRepository
import cz.muni.fi.rpg.model.firestore.COLLECTION_SKILLS
import kotlinx.coroutines.tasks.await
import java.util.UUID

internal class FirestoreSkillRepository(
    firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Skill>,
) : FirestoreCharacterItemRepository<Skill>(COLLECTION_SKILLS, mapper, firestore), SkillRepository {
    override suspend fun findByCompendiumId(
        characterId: CharacterId,
        compendiumSkillId: UUID
    ): Skill? {
        return itemCollection(characterId)
            .whereEqualTo("compendiumId", compendiumSkillId.toString())
            .get()
            .await()
            .documents
            .map { mapper.fromDocumentSnapshot(it) }
            .firstOrNull()
    }
}
