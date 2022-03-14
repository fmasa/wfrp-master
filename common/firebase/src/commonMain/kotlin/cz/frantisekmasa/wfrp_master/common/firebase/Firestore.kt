package cz.frantisekmasa.wfrp_master.common.firebase

expect class Firestore {
    fun collection(collectionPath: String): CollectionReference
    fun document(documentPath: String): DocumentReference
}
