package cz.frantisekmasa.wfrp_master.common.firebase

import com.google.cloud.firestore.DocumentSnapshot as FirebaseDocumentSnapshot

actual class DocumentSnapshot(
    private val snapshot: FirebaseDocumentSnapshot,
) {
    actual val data: Map<String, Any?> get() = snapshot.data ?: error("Snapshot data shouldn't be null")
}
