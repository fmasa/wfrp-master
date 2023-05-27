package cz.frantisekmasa.wfrp_master.common.firebase.functions

import io.ktor.client.HttpClient

actual class CloudFunctions(
    private val token: String,
    private val projectId: String,
    private val region: String,
    private val http: HttpClient,
) {
    actual fun getHttpsCallable(name: String): HttpsCallableReference {
        return HttpsCallableReference(
            url = "https://$region-$projectId.cloudfunctions.net/$name",
            token = token,
            http = http,
        )
    }
}
