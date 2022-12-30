package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import cz.frantisekmasa.wfrp_master.common.firebase.await

actual class Firestore(
    private val firestore: com.google.cloud.firestore.Firestore
) {
    actual fun collection(collectionPath: String) = CollectionReference(firestore.collection(collectionPath))

    actual fun document(documentPath: String) = DocumentReference(firestore.document(documentPath))

    actual suspend fun runTransaction(block: suspend (Transaction) -> Unit) {
        val batch = firestore.batch()

        block(Transaction(batch))

        batch.commit().await()
    }
}
