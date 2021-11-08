package cz.frantisekmasa.wfrp_master.core.firestore

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <T : Any> queryFlow(
    query: Query,
    snapshotParser: AggregateMapper<T>,
): Flow<List<T>> = callbackFlow {
    Napier.d("Attaching document listener for query $query")

    val listener = query.addSnapshotListener { snapshot, exception ->
        exception?.let { throw it }
        snapshot?.let {
            launch {
                val items = withContext(Dispatchers.Default) {
                    snapshot.map { async { snapshotParser.fromDocumentSnapshot(it) } }
                        .awaitAll()
                }

                withContext(Dispatchers.Main) { this@callbackFlow.trySend(items).isSuccess }
            }
        }
    }

    awaitClose {
        Napier.d("Detaching document listener for query $query")
        listener.remove()
    }
}

fun <T : Any> documentFlow(
    document: DocumentReference,
    snapshotProcessor: (result: Either<FirebaseFirestoreException?, DocumentSnapshot>) -> T,
): Flow<T> = callbackFlow {
    Napier.d("Attaching document listener for ${document.path}")

    val listener = document.addSnapshotListener { snapshot, exception ->
        exception?.let { trySend(snapshotProcessor(Left(it))) }
        snapshot?.let {
            launch {

                val data = withContext(Dispatchers.Default) {
                    snapshotProcessor(if (snapshot.exists()) Right(it) else Left(null))
                }
                withContext(Dispatchers.Main) { this@callbackFlow.trySend(data).isSuccess }
            }
        }
    }

    awaitClose {
        Napier.d("Detaching document listener for ${document.path}")
        listener.remove()
    }
}
