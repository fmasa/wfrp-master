package cz.frantisekmasa.wfrp_master.common.firebase.functions

expect class HttpsCallableReference {
    suspend fun call(data: Any): HttpsCallableResult
}
