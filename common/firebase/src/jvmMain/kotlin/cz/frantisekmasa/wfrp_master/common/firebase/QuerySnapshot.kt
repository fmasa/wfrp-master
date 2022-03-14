package cz.frantisekmasa.wfrp_master.common.firebase

import com.google.cloud.firestore.QuerySnapshot as FirebaseQuerySnapshot

actual class QuerySnapshot(
    private val querySnapshot: FirebaseQuerySnapshot
) {
    actual val documents: List<DocumentSnapshot>
        get() = querySnapshot.documents.map { DocumentSnapshot(it) }
}
