package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import kotlinx.coroutines.flow.Flow

expect class DocumentReference {
    val snapshots: Flow<Result<DocumentSnapshot>>

    fun collection(collectionPath: String): CollectionReference

    /**
     * @throws FirestoreException
     */
    suspend fun get(source: Source = Source.DEFAULT): DocumentSnapshot

    suspend fun delete()

    suspend fun set(fields: Map<String, Any?>, setOptions: SetOptions)

    suspend fun update(field: String, value: Any?)
}
