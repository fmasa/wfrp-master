package cz.frantisekmasa.wfrp_master.common.core.firebase

import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.DocumentSnapshot

enum class Source {
    DEFAULT,
    SERVER,
}

expect suspend fun DocumentReference.get(source: Source): DocumentSnapshot
