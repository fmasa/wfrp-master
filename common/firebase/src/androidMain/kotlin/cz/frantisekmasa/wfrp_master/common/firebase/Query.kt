package cz.frantisekmasa.wfrp_master.common.firebase

import com.google.firebase.firestore.Query
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual open class Query(
    private val query: Query
) {
    actual val snapshots: Flow<QuerySnapshot> = callbackFlow {
        Napier.d("Attaching document listener for query $query")

        val listener = query.addSnapshotListener { snapshot, exception ->
            exception?.let { throw it }
            snapshot?.let { trySend(QuerySnapshot(it)) }
        }

        awaitClose {
            Napier.d("Detaching document listener for query $query")
            listener.remove()
        }
    }

    actual suspend fun get() = QuerySnapshot(query.get().await())
}

