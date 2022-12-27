package cz.frantisekmasa.wfrp_master.common.firebase.firestore

expect class DocumentSnapshot {
    val id: String
    val data: Map<String, Any?>?
}
