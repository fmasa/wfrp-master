package cz.muni.fi.rpg.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.google.firebase.auth.FirebaseAuth
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.AboutDialog
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
            startActivity(Intent(this, StartupActivity::class.java))

            Toast.makeText(applicationContext, "You have been logged out", Toast.LENGTH_SHORT)
                .show()
            finish()
            return
        }

        userId = user.uid
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.overflow_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionAbout) {
            AboutDialog().show(supportFragmentManager, "AboutDialog")
        }

        return super.onOptionsItemSelected(item)
    }

    protected fun getUserId() = userId
}