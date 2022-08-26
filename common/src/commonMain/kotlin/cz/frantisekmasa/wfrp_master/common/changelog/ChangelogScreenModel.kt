package cz.frantisekmasa.wfrp_master.common.changelog

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
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
            http.get("https://gitlab.com/api/v4/projects/17038644/releases")
                .body<List<GitlabRelease>>()
                .map {
                    Release(
                        name = it.name,
                        description = it.description,
                    )
                }
        } catch (e: Exception) {
            Napier.d(e.message ?: "Unknown error while loading releases", e)
            null
        }
    }

    @Serializable
    private data class GitlabRelease(
        val name: String,
        val description: String,
    )

    @Immutable
    @Parcelize
    data class Release(
        val name: String,
        val description: String,
    ) : Parcelable
}