package cz.frantisekmasa.wfrp_master.common.core.firebase

import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Source as NativeSource

actual suspend fun DocumentReference.get(source: Source): DocumentSnapshot {
    return DocumentSnapshot(
        android.get(
            when (source) {
                Source.SERVER -> NativeSource.SERVER
                Source.DEFAULT -> NativeSource.DEFAULT
            }
        ).await()
    )
}
