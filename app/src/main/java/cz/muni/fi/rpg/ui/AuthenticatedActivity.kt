package cz.muni.fi.rpg.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

abstract class AuthenticatedActivity(@LayoutRes contentLayoutId: Int) :
    AppCompatActivity(contentLayoutId) {

    private val auth: FirebaseAuth by inject()

    private lateinit var userId: String

    private val authViewModel: AuthenticationViewModel by viewModel()

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

            Toast.makeText(applicationContext, "You have been logged out", Toast.LENGTH_SHORT)
                .show()
            finish()
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

        Toast.makeText(applicationContext, "You have been logged out", Toast.LENGTH_SHORT)
            .show()
        finish()
    }
}