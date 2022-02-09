package cz.frantisekmasa.wfrp_master.common.core.firestore.repositories

import arrow.core.Either
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Feature
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.firestore.AggregateMapper
import cz.frantisekmasa.wfrp_master.common.core.firestore.COLLECTION_CHARACTERS
import cz.frantisekmasa.wfrp_master.common.core.firestore.COLLECTION_FEATURES
import cz.frantisekmasa.wfrp_master.common.core.firestore.COLLECTION_PARTIES
import cz.frantisekmasa.wfrp_master.common.core.firestore.documentFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.tasks.await

/* internal */ class FirestoreCharacterFeatureRepository<T : Any>(
    feature: Feature,
    firestore: FirebaseFirestore,
    private val defaultValue: T,
    private val mapper: AggregateMapper<T>
) : CharacterFeatureRepository<T> {

    private val documentId = feature.name.lowercase()
    private val parties = firestore.collection(COLLECTION_PARTIES)

    override suspend fun save(characterId: CharacterId, feature: T) {
        val data = mapper.toDocumentData(feature)

        Napier.d("Saving $documentId for character $characterId to firestore")
        document(characterId).set(data, SetOptions.merge()).await()
    }

    override fun getLive(characterId: CharacterId) = documentFlow(document(characterId)) {
        it.fold(
            { e -> if (e == null)
                Either.Right(defaultValue)
            else Either.Left(CouldNotConnectToBackend(e))
            },
            { snapshot -> Either.Right(mapper.fromDocumentSnapshot(snapshot)) }
        )
    }

    private fun document(characterId: CharacterId): DocumentReference {
        return parties.document(characterId.partyId.toString())
            .collection(COLLECTION_CHARACTERS)
            .document(characterId.id)
            .collection(COLLECTION_FEATURES)
            .document(documentId)
    }
}
