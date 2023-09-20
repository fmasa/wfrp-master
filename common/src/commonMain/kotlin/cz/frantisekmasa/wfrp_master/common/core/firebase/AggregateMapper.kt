package cz.frantisekmasa.wfrp_master.common.core.firebase

/**
 * Maps instances of aggregates to and from document data
 */
interface AggregateMapper<T : Any> {

    /**
     * Hydrates instance of T from data returned by Firestore
     */
    fun fromDocumentData(data: Map<String, Any?>): T

    /**
     * Converts instance of T to representation that can be saved to Firestore
     */
    fun toDocumentData(aggregate: T): Map<String, Any?>
}
