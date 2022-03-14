package cz.frantisekmasa.wfrp_master.common.firebase

expect class CollectionReference : Query {
    fun document(documentPath: String): DocumentReference
}
