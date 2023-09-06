package cz.frantisekmasa.wfrp_master.common.changelog

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

class ChangelogScreenModel(
    private val http: HttpClient,
) : ScreenModel {

    suspend fun loadReleases(): List<Release>? {
        return try {
            http.get("https://api.github.com/repos/fmasa/wfrp-master/releases?per_page=100")
                .body<List<GithubRelease>>()
                .map {
                    Release(
                        name = it.name,
                        description = it.body,
                    )
                }
        } catch (e: Exception) {
            Napier.d(e.message ?: "Unknown error while loading releases", e)
            null
        }
    }

    @Serializable
    private data class GithubRelease(
        val name: String,
        val body: String,
    )

    @Immutable
    @Parcelize
    data class Release(
        val name: String,
        val description: String,
    ) : Parcelable
}
