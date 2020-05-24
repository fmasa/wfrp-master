package cz.muni.fi.rpg.model.firestore

import com.google.firebase.firestore.DocumentSnapshot

internal typealias DocumentData = Map<String, Any>

/**
 * Maps instances of aggregates to and from document data
 */
internal interface AggregateMapper<T : Any> {

    /**
     * Hydrates instance of T from data returned by Firestore
     */
    fun fromDocumentSnapshot(snapshot: DocumentSnapshot): T

    /**
     * Converts instance of T to representation that can be saved to Firestore
     */
    fun toDocumentData(aggregate: T): DocumentData
}