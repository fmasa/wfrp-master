package cz.frantisekmasa.wfrp_master.common.firebase


actual class CollectionReference(
    private val collection: com.google.firebase.firestore.CollectionReference
): Query(collection) {
    actual fun document(documentPath: String): DocumentReference {
        return DocumentReference(collection.document(documentPath))
    }
}
