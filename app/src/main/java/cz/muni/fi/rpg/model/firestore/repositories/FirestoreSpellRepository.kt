package cz.muni.fi.rpg.model.firestore.repositories

import com.google.firebase.firestore.FirebaseFirestore
import cz.frantisekmasa.wfrp_master.core.firestore.AggregateMapper
import cz.frantisekmasa.wfrp_master.core.firestore.queryFlow
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.model.domain.spells.SpellRepository
import cz.muni.fi.rpg.model.firestore.*
import kotlinx.coroutines.tasks.await
import java.util.*

internal class FirestoreSpellRepository(
    private val firestore: FirebaseFirestore,
    private val mapper: AggregateMapper<Spell>
) : SpellRepository {
    override fun findAllForCharacter(characterId: CharacterId) = queryFlow(
        spellsCollection(characterId),
        mapper,
    )

    override suspend fun remove(characterId: CharacterId, spellId: UUID) {
        spellsCollection(characterId).document(spellId.toString()).delete().await()
    }

    override suspend fun save(characterId: CharacterId, spell: Spell) {
        spellsCollection(characterId)
            .document(spell.id.toString())
            .set(mapper.toDocumentData(spell))
    }

    private fun spellsCollection(characterId: CharacterId) =
        firestore.collection(COLLECTION_PARTIES)
            .document(characterId.partyId.toString())
            .collection(COLLECTION_CHARACTERS)
            .document(characterId.id)
            .collection(COLLECTION_SPELLS)
}