package cz.frantisekmasa.wfrp_master.common.core.firebase.repositories

import arrow.core.left
import arrow.core.right
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Feature
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.firebase.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firebase.Schema
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.DocumentReference
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Firestore
import cz.frantisekmasa.wfrp_master.common.firebase.firestore.SetOptions
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map

class FirestoreCharacterFeatureRepository<T : Any>(
    feature: Feature,
    firestore: Firestore,
    private val defaultValue: T,
    private val mapper: AggregateMapper<T>
) : CharacterFeatureRepository<T> {

    private val documentId = feature.name.lowercase()
    private val parties = firestore.collection(Schema.Parties)

    override suspend fun save(characterId: CharacterId, feature: T) {
        val data = mapper.toDocumentData(feature)

        Napier.d("Saving $documentId for character $characterId to firestore")
        document(characterId).set(data, SetOptions.MERGE)
    }

    override fun getLive(characterId: CharacterId) =
        document(characterId)
            .snapshots
            .map { snapshot ->
                snapshot.fold(
                    { (it.data?.let(mapper::fromDocumentData) ?: defaultValue).right() },
                    { CouldNotConnectToBackend(it).left() },
                )
            }

    private fun document(characterId: CharacterId): DocumentReference {
        return parties.document(characterId.partyId.toString())
            .collection(Schema.Characters)
            .document(characterId.id)
            .collection(Schema.CharacterFeatures)
            .document(documentId)
    }
}
