package cz.muni.fi.rpg.model.firestore.repositories

import com.google.firebase.firestore.FirebaseFirestore
import cz.frantisekmasa.wfrp_master.core.firestore.AggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.queryFlow
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.model.domain.talents.TalentRepository
import cz.muni.fi.rpg.model.firestore.*
import kotlinx.coroutines.tasks.await
import java.util.*

internal class FirestoreTalentRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Talent>
) : TalentRepository {
    override fun findAllForCharacter(characterId: CharacterId) = queryFlow(
        talentsCollection(characterId),
        mapper,
    )

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
            .document(characterId.id)
            .collection(COLLECTION_TALENTS)
}