package cz.frantisekmasa.wfrp_master.common.core.firebase

import cz.frantisekmasa.wfrp_master.common.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

fun <T : Any> Query.documents(aggregateMapper: AggregateMapper<T>): Flow<List<T>> {
    return snapshots.map { snapshot ->
        withContext(Dispatchers.Default) {
            snapshot.documents.map { async { aggregateMapper.fromDocumentSnapshot(it) } }
                .awaitAll()
        }
    }
}
