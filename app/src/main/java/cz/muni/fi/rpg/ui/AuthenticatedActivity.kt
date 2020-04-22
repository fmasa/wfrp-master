package cz.muni.fi.rpg.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class AuthenticatedActivity(@LayoutRes contentLayoutId: Int) :
    DaggerAppCompatActivity(contentLayoutId) {

    @Inject
    protected lateinit var auth: FirebaseAuth

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = auth.currentUser

        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))

            Toast.makeText(applicationContext, "You have been logged out", Toast.LENGTH_SHORT)
                .show()
            finish()
            return
        }

        userId = user.uid
    }

    protected fun getUserId() = userId
}