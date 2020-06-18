package cz.muni.fi.rpg.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthenticationViewModel(private val auth: FirebaseAuth) : ViewModel() {

    fun isAuthenticated() = auth.currentUser != null

    /**
     * @return true if user was successfully authenticated and false otherwise
     */
    suspend fun authenticateAnonymously(): Boolean {
        return try {
            auth.signInAnonymously().await()
            Log.d(null, "User has signed in successfully")

            true
        } catch (e: Throwable) {
            Log.e("AuthenticationViewModel", "Anonymous sign-in has failed", e)
            false
        }
    }

    fun getUserId() = auth.currentUser?.uid ?: error("User is not authenticated")
}