package cz.frantisekmasa.wfrp_master.common.firebase.firestore

expect class Firestore {
    fun collection(collectionPath: String): CollectionReference
    fun document(documentPath: String): DocumentReference
    suspend fun runTransaction(block: suspend (Transaction) -> Unit)
}
