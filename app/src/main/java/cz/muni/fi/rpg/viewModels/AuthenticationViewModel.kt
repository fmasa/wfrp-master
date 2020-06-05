package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthenticationViewModel(private val auth: FirebaseAuth) : ViewModel() {
    fun getUserId() = auth.currentUser?.uid ?: error("User is not authenticated")
}