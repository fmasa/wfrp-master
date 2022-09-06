package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import com.google.cloud.firestore.DocumentSnapshot as NativeDocumentSnapshot

actual class DocumentSnapshot(
    private val snapshot: NativeDocumentSnapshot,
) {
    actual val data: Map<String, Any?>? get() = snapshot.data
}
