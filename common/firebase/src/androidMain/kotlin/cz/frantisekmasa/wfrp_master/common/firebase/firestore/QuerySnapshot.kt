package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.firebase.firestore.QuerySnapshot as NativeQuerySnapshot

actual class QuerySnapshot(
    private val querySnapshot: NativeQuerySnapshot
) {
    actual val documents: List<DocumentSnapshot>
        get() = querySnapshot.documents.map { DocumentSnapshot(it) }
}