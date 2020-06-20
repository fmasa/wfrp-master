package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthenticationViewModel(private val auth: FirebaseAuth) : ViewModel() {

    fun isAuthenticated() = auth.currentUser != null

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
}