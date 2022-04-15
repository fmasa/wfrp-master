package cz.frantisekmasa.wfrp_master.common.core.firebase

import cz.frantisekmasa.wfrp_master.common.firebase.firestore.DocumentSnapshot

/**
 * Maps instances of aggregates to and from document data
 */
interface AggregateMapper<T : Any> {

    /**
     * Hydrates instance of T from data returned by Firestore
     */
    @Deprecated("Use fromDocumentData() instead as it allows you to handle nonexistent documents")
    fun fromDocumentSnapshot(snapshot: DocumentSnapshot): T

    fun fromDocumentData(data: Map<String, Any?>): T

    /**
     * Converts instance of T to representation that can be saved to Firestore
     */
    fun toDocumentData(aggregate: T): Map<String, Any?>
}
