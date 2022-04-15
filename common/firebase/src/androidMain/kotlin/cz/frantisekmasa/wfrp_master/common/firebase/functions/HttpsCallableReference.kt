package cz.frantisekmasa.wfrp_master.common.firebase.functions

import kotlinx.coroutines.tasks.await
import com.google.firebase.functions.HttpsCallableReference as NativeHttpsCallableReference

actual class HttpsCallableReference(
    private val httpsCallable: NativeHttpsCallableReference,
) {
    actual suspend fun call(data: Any): HttpsCallableResult {
        httpsCallable.call(data).await()

        return HttpsCallableResult
    }
}