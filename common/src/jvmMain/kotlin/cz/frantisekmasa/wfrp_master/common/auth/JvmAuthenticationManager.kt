package cz.frantisekmasa.wfrp_master.common.auth

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.gitlive.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

class JvmAuthenticationManager(
    common: CommonAuthenticationManager,
    private val auth: FirebaseAuth,
    private val http: HttpClient,
) {
    val user = common.user

    enum class SignInError {
        InvalidEmail,
        InvalidPassword,
        EmailNotFound,
        EmailNotVerified,
        TooManyAttempts,
        UnknownError,
    }

    suspend fun signIn(
        email: String,
        password: String,
    ): Either<SignInError, Unit> {
        val response =
            request(
                "accounts:signInWithPassword",
                SignInRequest(
                    email = email,
                    password = password,
                ),
            )

        return when (response) {
            is ApiResponse.Success -> {
                val idToken = response.body<SignInResponse>().idToken
                val userData = getUserData(idToken) ?: return SignInError.UnknownError.left()

                if (!userData.emailVerified) {
                    sendEmailVerification(idToken)
                    return SignInError.EmailNotVerified.left()
                }

                auth.signInWithEmailAndPassword(email, password)

                Unit.right()
            }

            is ApiResponse.FailureResponse -> {
                when {
                    response.error.message == "INVALID_EMAIL" -> SignInError.InvalidEmail
                    response.error.message == "INVALID_PASSWORD" -> SignInError.InvalidPassword
                    response.error.message == "EMAIL_NOT_FOUND" -> SignInError.EmailNotFound
                    response.error.message.startsWith("TOO_MANY_ATTEMPTS_TRY_LATER") -> SignInError.TooManyAttempts
                    else -> SignInError.UnknownError
                }.left()
            }
        }
    }

    @Serializable
    data class SignInResponse(
        val idToken: String,
    )

    private suspend fun getUserData(idToken: String): UserData? {
        val response =
            request(
                "accounts:lookup",
                GetUserDataRequest(
                    idToken = idToken,
                ),
            )

        return when (response) {
            is ApiResponse.Success -> {
                response.body<GetUserDataResponse>().users.firstOrNull()
            }
            is ApiResponse.FailureResponse -> {
                null
            }
        }
    }

    @Serializable
    data class GetUserDataRequest(
        val idToken: String,
    )

    @Serializable
    data class GetUserDataResponse(
        val users: List<UserData>,
    )

    @Serializable
    data class UserData(
        val emailVerified: Boolean,
    )

    suspend fun logout() {
        auth.signOut()
    }

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
        val response =
            http.post(
                "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=$API_KEY",
            ) {
                contentType(ContentType.Application.Json)
                setBody(
                    PasswordResetRequest(
                        requestType = "PASSWORD_RESET",
                        email = email,
                    ),
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

    suspend fun register(
        email: String,
        password: String,
    ): RegistrationResult {
        return when (val response = request("accounts:signUp", SignUpRequest(email, password))) {
            is ApiResponse.Success -> {
                sendEmailVerification(response.body<SignUpResponse>().idToken)
                RegistrationResult.Success
            }

            is ApiResponse.FailureResponse -> {
                when {
                    response.error.message == "EMAIL_EXISTS" -> RegistrationResult.EmailExists
                    response.error.message.startsWith(
                        "WEAK_PASSWORD",
                    ) -> RegistrationResult.WeakPassword(criteria = response.error.message.split(':')[1].trim())
                    response.error.message.startsWith("TOO_MANY_ATTEMPTS_TRY_LATER") -> RegistrationResult.TooManyAttempts
                    else -> RegistrationResult.UnknownError
                }
            }
        }
    }

    @Serializable
    private data class SignUpRequest(
        val email: String,
        val password: String,
        val returnSecureToken: Boolean = true,
    )

    @Serializable
    private data class SignUpResponse(
        val idToken: String,
    )

    sealed interface RegistrationResult {
        object Success : RegistrationResult

        object EmailExists : RegistrationResult

        object TooManyAttempts : RegistrationResult

        object UnknownError : RegistrationResult

        data class WeakPassword(val criteria: String) : RegistrationResult
    }

    /**
     * https://firebase.google.com/docs/reference/rest/auth#section-send-email-verification
     */
    private suspend fun sendEmailVerification(idToken: String) {
        request(
            "accounts:sendOobCode",
            SendEmailVerificationRequest(
                requestType = "VERIFY_EMAIL",
                idToken = idToken,
            ),
        )
    }

    @Serializable
    private data class SendEmailVerificationRequest(
        val requestType: String,
        val idToken: String,
    )

    private suspend inline fun <reified TRequest : Any> request(
        endpoint: String,
        body: TRequest,
    ): ApiResponse {
        val response =
            http.post(
                "https://identitytoolkit.googleapis.com/v1/$endpoint?key=$API_KEY",
            ) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

        return when (response.status) {
            HttpStatusCode.OK -> ApiResponse.Success(response)
            else -> response.body<ApiResponse.FailureResponse>()
        }
    }

    private sealed interface ApiResponse {
        data class Success(private val response: HttpResponse) : ApiResponse {
            suspend inline fun <reified T : Any> body(): T = response.body<T>()
        }

        @Serializable
        data class FailureResponse(val error: Error) : ApiResponse
    }

    @Serializable
    data class Error(
        val message: String,
    )

    companion object {
        private const val API_KEY = "AIzaSyDO4Y4wWcY4HdYcsp8zcLMpMjwUJ_9q3Fw"
    }
}
