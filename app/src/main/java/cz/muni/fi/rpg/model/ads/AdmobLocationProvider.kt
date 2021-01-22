package cz.muni.fi.rpg.model.ads

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
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