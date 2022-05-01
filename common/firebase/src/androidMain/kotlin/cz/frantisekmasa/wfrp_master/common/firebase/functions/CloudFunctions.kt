package cz.frantisekmasa.wfrp_master.common.firebase.functions

import com.google.firebase.functions.FirebaseFunctions

actual class CloudFunctions(
    private val functions: FirebaseFunctions
) {
    actual fun getHttpsCallable(name: String): HttpsCallableReference {
        return HttpsCallableReference(functions.getHttpsCallable(name))
    }
}