package cz.frantisekmasa.wfrp_master.common.firebase.functions

expect class CloudFunctions {
    fun getHttpsCallable(name: String): HttpsCallableReference
}