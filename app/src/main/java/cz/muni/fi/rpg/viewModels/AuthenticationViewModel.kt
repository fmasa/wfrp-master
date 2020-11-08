package cz.muni.fi.rpg.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.authentication.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthenticationViewModel(private val auth: FirebaseAuth) : ViewModel(),
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    val user: Flow<User> = callbackFlow {
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

    fun getGoogleSignInIntent(context: Context) = googleClient(context).signInIntent

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