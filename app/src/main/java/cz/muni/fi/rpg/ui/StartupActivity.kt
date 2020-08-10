package cz.muni.fi.rpg.ui

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.log.Reporter
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber


class StartupActivity : AppCompatActivity(R.layout.activity_startup),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        const val CODE_SIGN_IN = 1
    }

    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onStart() {
        super.onStart()

        launch {
            if (viewModel.isAuthenticated()) {
                showPartyList()
                return@launch
            }

            val context = applicationContext
            val token = viewModel.obtainGoogleToken(context)

            if (token == null) {
                Timber.d("Could not obtain Google account, starting Sign-In")
                withContext(Dispatchers.Main) {
                    startActivityForResult(viewModel.getGoogleSignInIntent(context), CODE_SIGN_IN)
                }
                return@launch
            }

            authenticateWithGoogle(token)
        }
    }

    private suspend fun authenticateWithGoogle(idToken: String) {
        if (viewModel.connectGoogleToFirebaseAuth(idToken)) {
            showPartyList()
            return
        }

        withContext(Dispatchers.Main) {
            fallbackToAnonymousAuthentication(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_SIGN_IN) {
            launch {
                try {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
                    authenticateWithGoogle(account.idToken ?: error("Token not set"))
                } catch (e: ApiException) {
                    Timber.e(e, "Google sign-in failed")
                    fallbackToAnonymousAuthentication(e.message)
                } catch (e : Throwable) {
                    Timber.e(e)
                    throw e
                }
            }
        }
    }

    private fun fallbackToAnonymousAuthentication(message: String?) {
        AlertDialog.Builder(this)
            .setMessage(message ?: getString(R.string.google_sign_in_error))
            .setNeutralButton(android.R.string.ok) { _, _ ->
                launch {
                    if (viewModel.authenticateAnonymously()) {
                        showPartyList()
                    } else {
                        withContext(Dispatchers.Main) { toast("Authentication failed.") }
                    }
                }
            }.show()
    }

    private suspend fun showPartyList() {
        withContext(Dispatchers.Main) {
            Reporter.setUserId(viewModel.getUserId())
            startActivity(Intent(this@StartupActivity, MainActivity::class.java))
            finish()
        }
    }
}
