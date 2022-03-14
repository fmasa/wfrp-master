package cz.frantisekmasa.wfrp_master.common.firebase

import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual class DocumentReference(
    private val document: com.google.firebase.firestore.DocumentReference,
) {
    actual val snapshots: Flow<DocumentSnapshot> = callbackFlow {
        Napier.d("Attaching document listener for ${document.path}")

        val listener = document.addSnapshotListener { snapshot, exception ->
            exception?.let { throw it }
            snapshot?.let { trySend(DocumentSnapshot(it)) }
        }

        awaitClose {
            Napier.d("Detaching document listener for ${document.path}")
            listener.remove()
        }
    }

    actual fun collection(collectionPath: String): CollectionReference {
        return CollectionReference(document.collection(collectionPath))
    }

    actual suspend fun get(): DocumentSnapshot = DocumentSnapshot(document.get().await())

    actual suspend fun delete() {
        document.delete().await()
    }
}

