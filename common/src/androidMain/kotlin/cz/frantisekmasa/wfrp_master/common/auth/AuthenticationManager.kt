package cz.frantisekmasa.wfrp_master.common.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import cz.frantisekmasa.wfrp_master.common.core.auth.User
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val LocalWebClientId = staticCompositionLocalOf<String> { error("LocalWebTokenId was not set") }

class AuthenticationManager(private val auth: FirebaseAuth) : UserProvider {
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val authenticated: StateFlow<Boolean?> = callbackFlow {
        trySend(auth.currentUser != null)

        val listener = FirebaseAuth.AuthStateListener {
            auth.currentUser?.let {
                launch(Dispatchers.Main) {
                    this@callbackFlow.trySend(auth.currentUser != null).isSuccess
                }
            }
        }

        auth.addAuthStateListener(listener)

        awaitClose { auth.removeAuthStateListener(listener) }
    }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

    val user: StateFlow<User?> = callbackFlow {
        auth.currentUser?.let { this.trySend(it).isSuccess }

        val listener = FirebaseAuth.AuthStateListener {
            auth.currentUser?.let {
                launch(Dispatchers.Main) {
                    trySend(it)
                }
            }
        }

        auth.addAuthStateListener(listener)

        awaitClose { auth.removeAuthStateListener(listener) }
    }.map {
        User(
            id = UserId(it.uid),
            email = if (it.email == "") null else it.email
        )
    }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

    override val userId: UserId? get() = user.value?.id

    init {
        coroutineScope.launch {
            user.collect { it?.let { Reporter.setUserId(it.id) } }
        }
    }

    suspend fun signInWithGoogleToken(idToken: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        Napier.d("Authenticated, idToken: $idToken")

        return try {
            auth.signInWithCredential(credential).await()

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

        user.linkWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
    }

    fun googleSignInContract(webClientId: String): ActivityResultContract<Int?, IntentResult> {
        return object : ActivityResultContract<Int?, IntentResult>() {

            override fun createIntent(context: Context, requestCode: Int?) =
                getGoogleSignInIntent(context, webClientId)

            override fun parseResult(resultCode: Int, intent: Intent?): IntentResult {
                Napier.d(resultCode.toString())
                return IntentResult(resultCode, intent)
            }
        }
    }

    private fun getGoogleSignInIntent(context: Context, webClientId: String): Intent {
        return googleClient(context, webClientId).signInIntent
    }

    suspend fun attemptToRestoreExistingGoogleSignIn(context: Context, webClientId: String): Boolean {
        val googleToken = obtainGoogleToken(context, webClientId)

        return googleToken != null && signInWithGoogleToken(googleToken)
    }

    private suspend fun obtainGoogleToken(context: Context, webClientId: String): String? {
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

    /**
     * @return true if user was successfully authenticated and false otherwise
     */
    suspend fun authenticateAnonymously(): Boolean {
        check(auth.currentUser == null) { "User is already authenticated" }

        return try {
            Napier.d("Starting Firebase anonymous sign in")
            auth.signInAnonymously().await()
            Napier.d("User has signed in successfully")

            true
        } catch (e: Throwable) {
            Napier.e("Anonymous sign-in has failed", e)
            false
        }
    }

    private fun googleClient(context: Context, webClientId: String) = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    )

    data class IntentResult(
        val resultCode: Int,
        val intent: Intent?,
    )
}
