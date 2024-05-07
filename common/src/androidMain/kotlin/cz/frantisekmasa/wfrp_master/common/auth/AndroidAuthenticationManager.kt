package cz.frantisekmasa.wfrp_master.common.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.GoogleAuthProvider
import io.github.aakira.napier.Napier
import kotlinx.coroutines.tasks.await

val LocalWebClientId = staticCompositionLocalOf<String> { error("LocalWebTokenId was not set") }

class AndroidAuthenticationManager(
    private val auth: FirebaseAuth,
    val common: CommonAuthenticationManager,
) {
    suspend fun signInWithGoogleToken(idToken: String): Boolean {
        val credential = GoogleAuthProvider.credential(idToken, null)

        Napier.d("Authenticated, idToken: $idToken")

        return try {
            auth.signInWithCredential(credential)

            true
        } catch (e: Throwable) {
            Napier.e("Connection of Google credentials to Firebase Auth failed", e)

            false
        }
    }

    /**
     * @throws FirebaseAuthUserCollisionException when Google account is already used for another account
     * @throws IllegalStateException when user is even anonymously authenticated.
     */
    suspend fun linkAccountToGoogle(idToken: String) {
        val user = auth.currentUser

        check(user != null)

        user.linkWithCredential(GoogleAuthProvider.credential(idToken, null))
    }

    fun googleSignInContract(webClientId: String): ActivityResultContract<Int?, IntentResult> {
        return object : ActivityResultContract<Int?, IntentResult>() {
            override fun createIntent(
                context: Context,
                input: Int?,
            ) = getGoogleSignInIntent(context, webClientId)

            override fun parseResult(
                resultCode: Int,
                intent: Intent?,
            ): IntentResult {
                Napier.d(resultCode.toString())
                return IntentResult(resultCode, intent)
            }
        }
    }

    private fun getGoogleSignInIntent(
        context: Context,
        webClientId: String,
    ): Intent {
        return googleClient(context, webClientId).signInIntent
    }

    suspend fun attemptToRestoreExistingGoogleSignIn(
        context: Context,
        webClientId: String,
    ): Boolean {
        val googleToken = obtainGoogleToken(context, webClientId)

        return googleToken != null && signInWithGoogleToken(googleToken)
    }

    private suspend fun obtainGoogleToken(
        context: Context,
        webClientId: String,
    ): String? {
        val lastAccount = GoogleSignIn.getLastSignedInAccount(context)

        if (lastAccount != null) {
            return lastAccount.idToken
        }

        return try {
            googleClient(context, webClientId).silentSignIn().await().idToken
        } catch (e: Throwable) {
            Napier.e(e.toString(), e)

            null
        }
    }

    private fun googleClient(
        context: Context,
        webClientId: String,
    ) = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build(),
    )

    data class IntentResult(
        val resultCode: Int,
        val intent: Intent?,
    )
}
