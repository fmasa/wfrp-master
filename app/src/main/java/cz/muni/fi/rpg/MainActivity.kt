package cz.muni.fi.rpg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance();

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
