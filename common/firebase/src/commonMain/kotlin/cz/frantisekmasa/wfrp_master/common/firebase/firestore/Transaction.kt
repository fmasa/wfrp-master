package cz.frantisekmasa.wfrp_master.common.firebase.firestore

expect class Transaction {
    fun set(documentReference: DocumentReference, fields: Map<String, Any?>, options: SetOptions)
    fun delete(documentReference: DocumentReference)
}