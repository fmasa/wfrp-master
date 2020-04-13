package cz.muni.fi.rpg.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.partyList.PartyListActivity
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser;

        if (currentUser != null) {
            showPartyList()
            return
        }

        auth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(null, "User has signed in successfully")

                showPartyList()
            } else {
                Log.e(null, "Anonymous sign-in has failed", task.exception);
                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPartyList() {
        startActivity(Intent(this, PartyListActivity::class.java))
        finish()
    }
}
