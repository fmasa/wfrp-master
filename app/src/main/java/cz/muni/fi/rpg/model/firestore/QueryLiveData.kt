package cz.muni.fi.rpg.model.firestore

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

internal class QueryLiveData<T: Any>(
    private val query: Query,
    private val snapshotParser: AggregateMapper<T>
) : MutableLiveData<List<T>>() {
    private var listener: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()

        listener = query.addSnapshotListener { snapshot, exception ->
            exception?.let { throw it }
            snapshot?.let {
                value = snapshot
                    .asIterable()
                    .map(snapshotParser::fromDocumentSnapshot)
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        listener?.remove()
    }
}