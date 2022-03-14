package cz.frantisekmasa.wfrp_master.common.firebase

import com.google.firebase.firestore.QuerySnapshot

actual class QuerySnapshot(
    private val querySnapshot: QuerySnapshot
) {
    actual val documents: List<DocumentSnapshot>
        get() = querySnapshot.documents.map { DocumentSnapshot(it) }
}
