package cz.frantisekmasa.wfrp_master.common.firebase.firestore

import cz.frantisekmasa.wfrp_master.common.firebase.await
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import com.google.cloud.firestore.Query as NativeQuery

actual open class Query(
    private val query: NativeQuery
) {
    actual val snapshots = callbackFlow {
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

    actual fun orderBy(field: String, direction: Direction): Query {
        return Query(query.orderBy(field, direction.toNative()))
    }


    actual enum class Direction(private val native: NativeQuery.Direction) {
        ASCENDING(NativeQuery.Direction.ASCENDING),
        DESCENDING(NativeQuery.Direction.DESCENDING);

        fun toNative() = native
    }

    actual suspend fun get() = QuerySnapshot(query.get().await())

    actual fun whereEqualTo(field: String, value: Any?): Query {
        return Query(query.whereEqualTo(field, value))
    }

    actual fun whereArrayContains(field: String, value: Any): Query {
        return Query(query.whereArrayContains(field, value))
    }
}