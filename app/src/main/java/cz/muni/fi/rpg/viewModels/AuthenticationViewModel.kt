package cz.muni.fi.rpg.viewModels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import cz.muni.fi.rpg.R
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthenticationViewModel(private val auth: FirebaseAuth) : ViewModel() {

    fun isAuthenticated() = auth.currentUser != null

    suspend fun connectGoogleToFirebaseAuth(idToken: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        return try {
            auth.signInWithCredential(credential).await()

            true
        } catch (e: Throwable) {
            Timber.e(e, "Connection Google credentials to Firebase Auth failed")

            false
        }
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