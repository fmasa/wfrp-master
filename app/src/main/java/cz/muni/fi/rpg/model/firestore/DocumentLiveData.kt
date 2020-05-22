package cz.muni.fi.rpg.model.firestore

import androidx.lifecycle.MutableLiveData
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration

internal class DocumentLiveData<T>(
    private val document: DocumentReference,
    private val snapshotProcessor: (result: Either<FirebaseFirestoreException?, DocumentSnapshot>) -> T
) : MutableLiveData<T>() {
    private var listener: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listener = document.addSnapshotListener { snapshot, exception ->
            exception?.let { value = snapshotProcessor(Left(it))}
            snapshot?.let {
                value = snapshotProcessor(if (snapshot.exists()) Right(it) else Left(null))
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        listener?.remove()
    }
}