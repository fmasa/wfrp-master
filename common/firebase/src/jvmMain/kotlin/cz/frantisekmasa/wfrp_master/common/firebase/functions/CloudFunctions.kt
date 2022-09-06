package cz.frantisekmasa.wfrp_master.common.firebase.functions

actual class CloudFunctions(token: String, projectId: String) {
    actual fun getHttpsCallable(name: String): HttpsCallableReference {
        TODO("Implement code that calls HTTP request")
        // see spec: https://firebase.google.com/docs/functions/callable-reference
    }
}
