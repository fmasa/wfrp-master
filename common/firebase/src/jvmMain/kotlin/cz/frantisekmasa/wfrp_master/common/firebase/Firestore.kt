package cz.frantisekmasa.wfrp_master.common.firebase

actual class Firestore(
    private val firestore: com.google.cloud.firestore.Firestore
) {
    actual fun collection(collectionPath: String) = CollectionReference(firestore.collection(collectionPath))
    actual fun document(documentPath: String) = DocumentReference(firestore.document(documentPath))
}
