package cz.muni.fi.rpg.ui

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import cz.muni.fi.rpg.R
import org.koin.android.ext.android.inject

class StartupActivity : AppCompatActivity(R.layout.activity_startup) {
    private val auth: FirebaseAuth by inject()

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            showPartyList()
            return
        }

        auth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(null, "User has signed in successfully")

                showPartyList()
            } else {
                Log.e(null, "Anonymous sign-in has failed", task.exception)
                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPartyList() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
