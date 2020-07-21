package cz.muni.fi.rpg.model.firestore

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class QueryLiveData<T: Any>(
    private val query: Query,
    private val snapshotParser: AggregateMapper<T>,
    private val filter: (item: T) -> Boolean = { true }
) : MutableLiveData<List<T>>(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private var listener: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()

        listener = query.addSnapshotListener { snapshot, exception ->
            exception?.let { throw it }
            snapshot?.let {
                launch {
                    val items = snapshot
                        .asIterable()
                        .map(snapshotParser::fromDocumentSnapshot)
                        .filter(filter)

                    withContext(Dispatchers.Main) { value = items }
                }
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        listener?.remove()
    }
}