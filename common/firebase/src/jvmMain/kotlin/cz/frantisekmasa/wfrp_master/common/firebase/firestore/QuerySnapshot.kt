package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.cloud.firestore.QuerySnapshot as NativeQuerySnapshot

actual class QuerySnapshot(
    private val querySnapshot: NativeQuerySnapshot
) {
    actual val documents: List<DocumentSnapshot>
        get() = querySnapshot.documents.map { DocumentSnapshot(it) }
}