package cz.muni.fi.rpg.viewModels

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import cz.frantisekmasa.wfrp_master.core.auth.User
import cz.frantisekmasa.wfrp_master.core.logging.Reporter
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.IntentResult
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class AuthenticationViewModel(private val auth: FirebaseAuth) : ViewModel() {
    val authenticated: StateFlow<Boolean?> = callbackFlow {
        offer(auth.currentUser != null)

        val listener = FirebaseAuth.AuthStateListener {
            auth.currentUser?.let {
                launch(Dispatchers.Main) {
                    offer(auth.currentUser != null)
                }
            }
        }

        auth.addAuthStateListener(listener)

        awaitClose { auth.removeAuthStateListener(listener) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val user: StateFlow<User?> = callbackFlow {
        auth.currentUser?.let { offer(it) }

        val listener = FirebaseAuth.AuthStateListener {
            auth.currentUser?.let {
                launch(Dispatchers.Main) {
                    offer(it)
                }
            }
        }

        auth.addAuthStateListener(listener)

        awaitClose { auth.removeAuthStateListener(listener) }
    }.map {
        User(
            id = it.uid,
            email = if (it.email == "") null else it.email
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            user.collect { it?.let { Reporter.setUserId(it.id) } }
        }
    }

    fun isAuthenticated() = auth.currentUser != null

    suspend fun signInWithGoogleToken(idToken: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        return try {
            auth.signInWithCredential(credential).await()

            true
        } catch (e: Throwable) {
            Timber.e(e, "Connection of Google credentials to Firebase Auth failed")

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

    fun googleSignInContract(): ActivityResultContract<Int?, IntentResult> {
        return object : ActivityResultContract<Int?, IntentResult>() {

            override fun createIntent(context: Context, requestCode: Int?) =
                getGoogleSignInIntent(context)

            override fun parseResult(resultCode: Int, intent: Intent?): IntentResult {
                Timber.d(resultCode.toString())
                return IntentResult(resultCode, intent)
            }
        }
    }

    fun getGoogleSignInIntent(context: Context) = googleClient(context).signInIntent

    suspend fun attemptToRestoreExistingGoogleSignIn(context: Context): Boolean {
        val googleToken = obtainGoogleToken(context)

        return googleToken != null && signInWithGoogleToken(googleToken)
    }

    suspend fun obtainGoogleToken(context: Context): String? {
        val lastAccount = GoogleSignIn.getLastSignedInAccount(context)

        if (lastAccount != null) {
            return lastAccount.idToken
        }

        return try {
            googleClient(context).silentSignIn().await().idToken
        } catch (e: Throwable) {
            Timber.e(e)

            null
        }
    }

    /**
     * @return true if user was successfully authenticated and false otherwise
     */
    suspend fun authenticateAnonymously(): Boolean {
        check(auth.currentUser == null) { "User is already authenticated" }

        return try {
            Timber.d("Starting Firebase anonymous sign in")
            auth.signInAnonymously().await()
            Timber.d("User has signed in successfully")

            true
        } catch (e: Throwable) {
            Timber.e(e, "Anonymous sign-in has failed")
            false
        }
    }

    fun getUserId() = auth.currentUser?.uid ?: error("User is not authenticated")

    private fun googleClient(context: Context) = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    )
}

@Composable
fun provideAuthenticationViewModel(): AuthenticationViewModel =
    AmbientActivity.current.getViewModel()