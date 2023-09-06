package cz.frantisekmasa.wfrp_master.common.firebase.firestore

expect class Firestore {
    fun collection(collectionPath: String): CollectionReference
    suspend fun runTransaction(block: suspend (Transaction) -> Unit)
}
