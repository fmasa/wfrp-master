package cz.frantisekmasa.wfrp_master.common.auth

import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.core.shared.edit
import cz.frantisekmasa.wfrp_master.common.core.shared.stringKey
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

class JvmAuthenticationManager(
    val common: CommonAuthenticationManager,
    private val auth: FirebaseAuth,
    private val http: HttpClient,
    private val settings: SettingsStorage,
) {
    val status: Flow<AuthenticationStatus?> = auth.authStateChanged.map { user ->
        if (user == null) {
            AuthenticationStatus.NotAuthenticated
        } else {
            AuthenticationStatus.Authenticated(user)
        }
    }

    sealed interface AuthenticationStatus {
        data class Authenticated(val user: FirebaseUser) : AuthenticationStatus
        object NotAuthenticated : AuthenticationStatus
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
        auth.signInWithCredential(GoogleAuthProvider.credential(body.idToken, null))

        return body
    }

    suspend fun logout() {
        auth.signOut()
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

    @Serializable
    data class Failure(val error: Error)

    /**
     * https://firebase.google.com/docs/reference/rest/auth#section-send-password-reset-email
     */
    suspend fun resetPassword(email: String): PasswordResetResult {
        val response = http.post(
            "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=$API_KEY"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                PasswordResetRequest(
                    requestType = "PASSWORD_RESET",
                    email = email,
                )
            )
        }

        if (response.status != HttpStatusCode.OK) {
            val body = response.body<Failure>()

            if (body.error.message == "EMAIL_NOT_FOUND") {
                return PasswordResetResult.EmailNotFound
            }

            return PasswordResetResult.UnknownError
        }

        return PasswordResetResult.Success
    }

    @Serializable
    private data class PasswordResetRequest(
        val requestType: String,
        val email: String,
    )

    sealed interface PasswordResetResult {
        object Success : PasswordResetResult
        object EmailNotFound : PasswordResetResult
        object UnknownError : PasswordResetResult
    }

    companion object {
        private val REFRESH_TOKEN = stringKey("firebase_refresh_token")
        private const val API_KEY = "AIzaSyDO4Y4wWcY4HdYcsp8zcLMpMjwUJ_9q3Fw"
    }
}
