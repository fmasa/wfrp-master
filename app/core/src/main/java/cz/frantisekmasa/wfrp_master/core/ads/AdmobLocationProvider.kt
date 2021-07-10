package cz.frantisekmasa.wfrp_master.core.ads

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import timber.log.Timber

class AdmobLocationProvider : LocationProvider {
    companion object {
        const val CONSENT_API_URL = "https://adservice.google.com/getconfig/pubvendors"
    }

    override suspend fun isUserInEeaOrUnknown(): Boolean {
        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = JacksonSerializer {
                    propertyNamingStrategy = PropertyNamingStrategy.SnakeCaseStrategy()
                }
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

private data class Response(
    val isRequestInEeaOrUnknown: Boolean
)
