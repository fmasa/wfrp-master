package cz.frantisekmasa.wfrp_master.common.firebase

import kotlinx.coroutines.flow.Flow

expect class DocumentReference {
    val snapshots: Flow<DocumentSnapshot>

    fun collection(collectionPath: String): CollectionReference
    suspend fun get(): DocumentSnapshot
    suspend fun delete()
}
