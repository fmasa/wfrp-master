package cz.muni.fi.rpg.model.infrastructure

import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import cz.muni.fi.rpg.model.firestore.SnapshotParser
import kotlin.reflect.KClass

class GsonSnapshotParser<T : Any>(
    private val modelClass: KClass<T>,
    private val gson: Gson
) : SnapshotParser<T> {

    override fun parseSnapshot(snapshot: DocumentSnapshot): T {
        return gson.fromJson(gson.toJsonTree(snapshot.data), modelClass.java);
    }
}