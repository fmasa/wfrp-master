package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

actual class Firestore(
    private val firestore: FirebaseFirestore
) {
    actual fun collection(collectionPath: String) = CollectionReference(firestore.collection(collectionPath))
    actual fun document(documentPath: String) = DocumentReference(firestore.document(documentPath))

    actual suspend fun runTransaction(block: (Transaction) -> Unit) {
        firestore.runTransaction {
            block(Transaction(it))
        }.await()
    }
}