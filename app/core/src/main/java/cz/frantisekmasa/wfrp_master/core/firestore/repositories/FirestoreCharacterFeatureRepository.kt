package cz.frantisekmasa.wfrp_master.core.firestore.repositories

import arrow.core.Left
import arrow.core.Right
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import cz.frantisekmasa.wfrp_master.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Feature
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.firestore.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

/* internal */ class FirestoreCharacterFeatureRepository<T : Any>(
    feature: Feature,
    firestore: FirebaseFirestore,
    private val defaultValue: T,
    private val mapper: AggregateMapper<T>
) : CharacterFeatureRepository<T> {

    private val documentId = feature.name.toLowerCase(Locale.ROOT)
    private val parties = firestore.collection(COLLECTION_PARTIES)

    override suspend fun save(characterId: CharacterId, feature: T) {
        val data = mapper.toDocumentData(feature)

        Timber.d("Saving $documentId for character $characterId to firestore")
        document(characterId).set(data, SetOptions.merge()).await()
    }

    override fun getLive(characterId: CharacterId) = documentFlow(document(characterId)) {
        it.fold(
            { e -> if (e == null) Right(defaultValue) else Left(CouldNotConnectToBackend(e)) },
            { snapshot -> Right(mapper.fromDocumentSnapshot(snapshot)) }
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