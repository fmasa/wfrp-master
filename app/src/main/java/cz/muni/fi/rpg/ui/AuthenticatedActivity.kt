package cz.muni.fi.rpg.ui

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import cz.muni.fi.rpg.ui.common.toast
import org.koin.android.ext.android.inject

abstract class AuthenticatedActivity :
    AppCompatActivity() {

    private val auth: FirebaseAuth by inject()

    private lateinit var userId: String

    private val authListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser == null) {
            goToStartup()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = auth.currentUser

        if (user == null) {
            goToStartup()
            return
        }

        userId = user.uid
    }

    override fun onStart() {
        super.onStart()

        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()

        auth.removeAuthStateListener(authListener)
    }

    fun getUserId() = userId

    private fun goToStartup() {
        startActivity(Intent(this, StartupActivity::class.java))

        toast("You have been logged out")
        finish()
    }
}