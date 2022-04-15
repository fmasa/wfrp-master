package cz.frantisekmasa.wfrp_master.common.firebase.firestore

actual class CollectionReference(
    private val collection: com.google.cloud.firestore.CollectionReference,
) : Query(collection) {

   actual fun document(documentPath: String): DocumentReference {
       return DocumentReference(collection.document(documentPath))
   }
}