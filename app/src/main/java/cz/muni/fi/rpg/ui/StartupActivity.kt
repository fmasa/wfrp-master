package cz.muni.fi.rpg.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.AuthenticationFragment

class StartupActivity : AppCompatActivity(R.layout.activity_startup),
    AuthenticationFragment.Listener {

    override fun onStart() {
        super.onStart()

        supportFragmentManager.commitNow {
            add(AuthenticationFragment(), "Authentication")
        }
    }

    override fun onAuthenticated(userId: String) {
        startActivity(Intent(this@StartupActivity, MainActivity::class.java))
        finish()
    }
}
