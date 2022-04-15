package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.SkillRepository
import cz.frantisekmasa.wfrp_master.common.core.firebase.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore

class FirestoreSkillRepository(
    firestore: Firestore,
    mapper: AggregateMapper<Skill>,
) : FirestoreCharacterItemRepository<Skill>(Schema.Character.Skills, mapper, firestore),
    SkillRepository {
    override suspend fun findByCompendiumId(
        characterId: CharacterId,
        compendiumSkillId: Uuid
    ): Skill? {
        return itemCollection(characterId)
            .whereEqualTo("compendiumId", compendiumSkillId.toString())
            .get()
            .documents
            .firstOrNull()
            ?.data
            ?.let { mapper.fromDocumentData(it) }
    }
}
