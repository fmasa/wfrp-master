package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import kotlinx.coroutines.flow.Flow

expect open class Query {
    val snapshots: Flow<QuerySnapshot>

    fun orderBy(field: String, direction: Direction = Direction.ASCENDING): Query

    enum class Direction {
        ASCENDING,
        DESCENDING,
    }

    suspend fun get(): QuerySnapshot

    fun whereEqualTo(field: String, value: Any?): Query

    fun whereArrayContains(field: String, value: Any): Query
}