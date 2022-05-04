package cz.frantisekmasa.wfrp_master.common.auth

import cz.frantisekmasa.wfrp_master.common.FirebaseTokenHolder
import cz.frantisekmasa.wfrp_master.common.core.auth.User
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.core.shared.stringSettingsKey
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AuthenticationManager(
    private val http: HttpClient,
    private val tokenHolder: FirebaseTokenHolder,
    private val settings: SettingsStorage,
) {
    private val statusFlow = MutableStateFlow<AuthenticationStatus?>(null)

    val status: Flow<AuthenticationStatus?> get() = statusFlow

    sealed interface AuthenticationStatus {
        data class Authenticated(val user: User) : AuthenticationStatus
        object NotAuthenticated : AuthenticationStatus
    }

    suspend fun refreshUser(): UserRefreshResult {
        val refreshToken = settings[REFRESH_TOKEN]

        if (refreshToken == null) {
            statusFlow.emit(AuthenticationStatus.NotAuthenticated)
            return UserRefreshResult.NO_VALID_TOKEN
        }

        updateIdToken(getIdToken(refreshToken))

        return UserRefreshResult.SUCCESS
    }

    private suspend fun getIdToken(refreshToken: String): String {
        val response: IdTokenResponse = http.post("https://securetoken.googleapis.com/v1/token?key=$API_KEY") {
            contentType(ContentType.Application.Json)
            setBody(IdTokenRequest(refreshToken))
        }.body()

        settings.edit(REFRESH_TOKEN, response.refreshToken)

        return response.idToken
    }

    @Serializable
    data class IdTokenRequest(
        @SerialName("refresh_token")
        val refreshToken: String,
        @SerialName("grant_type")
        val grantType: String = "refresh_token",
    )

    @Serializable
    data class IdTokenResponse(
        @SerialName("refresh_token")
        val refreshToken: String,
        @SerialName("id_token")
        val idToken: String,
    )

    private suspend fun updateIdToken(idToken: String) {
        tokenHolder.setToken(idToken)

        val response: GetUserDataResponse = http.post(
            "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=$API_KEY"
        ) {
            contentType(ContentType.Application.Json)
            setBody(GetUserDataRequest(idToken))
        }.body()

        response.users.firstOrNull()?.let { statusFlow.emit(AuthenticationStatus.Authenticated(it)) }
    }

    @Serializable
    data class GetUserDataRequest(val idToken: String)

    @Serializable
    data class GetUserDataResponse(val users: List<User>)

    enum class UserRefreshResult {
        NO_VALID_TOKEN,
        SUCCESS
    }

    suspend fun signIn(email: String, password: String): SignInResponse {
        val response = http.post(
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$API_KEY"
        ) {
            contentType(ContentType.Application.Json)
            setBody(SignInRequest(email, password))
        }

        if (response.status != HttpStatusCode.OK) {
            return response.body<SignInResponse.Failure>()
        }

        val body: SignInResponse.Success = response.body()

        settings.edit(REFRESH_TOKEN, body.refreshToken)
        updateIdToken(body.idToken)

        return body
    }

    @Serializable
    sealed class SignInResponse {
        @Serializable
        data class Success(
            val refreshToken: String,
            val idToken: String,
        ) : SignInResponse()

        @Serializable
        data class Failure(val error: Error) : SignInResponse() {
            val isInvalidEmail: Boolean get() = error.message == "INVALID_EMAIL"
            val isInvalidPassword: Boolean get() = error.message == "INVALID_PASSWORD"
            val isEmailNotFound: Boolean get() = error.message == "EMAIL_NOT_FOUND"
        }
    }

    @Serializable
    data class Error(
        val message: String,
    )

    @Serializable
    data class SignInRequest(
        val email: String,
        val password: String,
        val returnSecureToken: Boolean = true,
    )

    companion object {
        private val REFRESH_TOKEN = stringSettingsKey("firebase_refresh_token")
        private const val API_KEY = "AIzaSyDO4Y4wWcY4HdYcsp8zcLMpMjwUJ_9q3Fw"
    }
}
