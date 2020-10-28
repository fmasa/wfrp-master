package cz.muni.fi.rpg.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.AuthenticationFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class StartupActivity : AppCompatActivity(R.layout.activity_startup),
    AuthenticationFragment.Listener {

    override fun onStart() {
        super.onStart()

        supportFragmentManager.beginTransaction().apply {
            add(AuthenticationFragment(), "Authentication")
            commit()
        }
    }

    override fun onAuthenticated(userId: String) {
        startActivity(Intent(this@StartupActivity, MainActivity::class.java))
        finish()
    }
}
