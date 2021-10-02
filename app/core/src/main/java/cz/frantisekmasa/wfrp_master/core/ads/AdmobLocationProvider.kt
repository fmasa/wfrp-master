package cz.frantisekmasa.wfrp_master.core.ads

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber

class AdmobLocationProvider : LocationProvider {
    companion object {
        const val CONSENT_API_URL = "https://adservice.google.com/getconfig/pubvendors"
    }

    override suspend fun isUserInEeaOrUnknown(): Boolean {
        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }

        return try {
            client.get<Response>(CONSENT_API_URL).isRequestInEeaOrUnknown
        } catch (e: Throwable) {
            Timber.e(e)

            true
        }
    }
}

@Serializable
private data class Response(
    @SerialName("is_request_in_eea_or_unknown")
    val isRequestInEeaOrUnknown: Boolean
)
