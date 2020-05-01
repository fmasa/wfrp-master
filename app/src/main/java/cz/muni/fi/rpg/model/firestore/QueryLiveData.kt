package cz.muni.fi.rpg.model.firestore

import androidx.lifecycle.MutableLiveData
import arrow.core.Left
import arrow.core.Right
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

internal class QueryLiveData<T>(
    private val query: Query,
    private val snapshotParser: SnapshotParser<T>
) : MutableLiveData<List<T>>() {
    private var listener: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()

        listener = query.addSnapshotListener { snapshot, exception ->
            exception?.let { throw it }
            snapshot?.let {
                value = snapshot
                    .asIterable()
                    .map(snapshotParser::parseSnapshot)
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        listener?.remove()
    }
}