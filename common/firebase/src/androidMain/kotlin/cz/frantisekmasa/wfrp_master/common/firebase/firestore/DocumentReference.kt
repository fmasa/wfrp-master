package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestoreException
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentReference as NativeDocumentReference

actual class DocumentReference(
    private val document: NativeDocumentReference,
) {
    actual val snapshots = callbackFlow<Result<DocumentSnapshot>> {
        Napier.d("Attaching document listener for ${document.path}")

        val listener = document.addSnapshotListener { snapshot, exception ->
            exception?.let { trySend(Result.failure(it)) }
            snapshot?.let { trySend(Result.success(DocumentSnapshot(it))) }
        }

        awaitClose {
            Napier.d("Detaching document listener for ${document.path}")
            listener.remove()
        }
    }

    actual fun collection(collectionPath: String): CollectionReference {
        return CollectionReference(document.collection(collectionPath))
    }

    actual suspend fun get(source: Source): DocumentSnapshot {
        try {
            return DocumentSnapshot(document.get(source.toNative()).await())
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreException(e, e.code == FirebaseFirestoreException.Code.UNAVAILABLE)
        }
    }

    actual suspend fun delete() {
        document.delete().await()
    }

    actual suspend fun set(fields: Map<String, Any?>, setOptions: SetOptions) {
        document.set(fields, setOptions.toNative()).await()
    }

    actual suspend fun update(field: String, value: Any?) {
        document.update(field, value).await()
    }

    fun toNative(): NativeDocumentReference = document
}
