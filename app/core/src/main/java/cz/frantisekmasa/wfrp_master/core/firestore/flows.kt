package cz.frantisekmasa.wfrp_master.core.firestore

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

fun <T : Any> queryFlow(
    query: Query,
    snapshotParser: AggregateMapper<T>,
): Flow<List<T>> = callbackFlow {
    val listener = query.addSnapshotListener { snapshot, exception ->
        exception?.let { throw it }
        snapshot?.let {
            launch {
                val items = withContext(Dispatchers.Default) {
                    snapshot.map(snapshotParser::fromDocumentSnapshot)
                }

                withContext(Dispatchers.Main) { offer(items) }
            }
        }
    }

    awaitClose { listener.remove() }
}

fun <T : Any> documentFlow(
    document: DocumentReference,
    snapshotProcessor: (result: Either<FirebaseFirestoreException?, DocumentSnapshot>) -> T,
): Flow<T> = callbackFlow {
    val listener = document.addSnapshotListener { snapshot, exception ->
        exception?.let { offer(snapshotProcessor(Left(it))) }
        snapshot?.let {
            launch {

                val data = withContext(Dispatchers.Default) {
                    snapshotProcessor(if (snapshot.exists()) Right(it) else Left(null))
                }
                withContext(Dispatchers.Main) { offer(data) }
            }
        }
    }

    awaitClose { listener.remove() }
}