package cz.frantisekmasa.wfrp_master.common.firebase.firestore

expect class CollectionReference : Query {
    fun document(documentPath: String): DocumentReference
}