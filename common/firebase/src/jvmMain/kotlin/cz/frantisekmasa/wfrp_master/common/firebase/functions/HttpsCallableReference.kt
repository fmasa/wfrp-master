package cz.frantisekmasa.wfrp_master.common.firebase.functions

import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

actual class HttpsCallableReference(
    private val url: String,
    private val token: String,
    private val http: HttpClient,
) {
    actual suspend fun call(data: Any): HttpsCallableResult {
        val response = http.post(url) {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(mapOf("data" to data))
        }

        if (response.status.value >= 300) {
            throw Exception("Cloud function returned ${response.status} (${response.bodyAsText()}")
        }

        return HttpsCallableResult
    }
}
