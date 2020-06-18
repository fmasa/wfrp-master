package cz.muni.fi.rpg.ui

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel

class StartupActivity : AppCompatActivity(R.layout.activity_startup),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onStart() {
        super.onStart()

        launch {
            if (viewModel.isAuthenticated() || viewModel.authenticateAnonymously()) {
                showPartyList()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun showPartyList() {
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@StartupActivity, MainActivity::class.java))
            finish()
        }
    }
}
