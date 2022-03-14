package cz.frantisekmasa.wfrp_master.common.firebase

import kotlinx.coroutines.flow.Flow

expect open class Query {
    val snapshots: Flow<QuerySnapshot>

    suspend fun get(): QuerySnapshot
}
