package cz.muni.fi.rpg.model.firestore

import com.google.firebase.firestore.DocumentSnapshot

internal interface SnapshotParser<T> {
    fun parseSnapshot(snapshot: DocumentSnapshot): T
}
