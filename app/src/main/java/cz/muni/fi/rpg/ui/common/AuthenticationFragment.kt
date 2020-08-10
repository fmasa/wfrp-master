package cz.muni.fi.rpg.ui.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.log.Reporter
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

/**
 * This fragment takes care of all authentication methods available to user
 *
 * Every entrypoint to application should implement AuthenticationFragment.Listener and add instance
 * of this fragment to FragmentManager
 *
 * @see AuthenticationFragment.Listener
 */
class AuthenticationFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        const val CODE_SIGN_IN = 1
    }

    interface Listener {
        fun onAuthenticated(userId: String)
    }

    private val viewModel: AuthenticationViewModel by viewModel()
    private lateinit var listener: Listener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        require(context is Listener)
        listener = context
    }

    private suspend fun authenticateWithGoogle(idToken: String) {
        if (viewModel.connectGoogleToFirebaseAuth(idToken)) {
            notifyDependentsThatUserIsAuthenticated()
            return
        }

        withContext(Dispatchers.Main) {
            fallbackToAnonymousAuthentication(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launch {
            if (viewModel.isAuthenticated()) {
                notifyDependentsThatUserIsAuthenticated()
                return@launch
            }

            val context = requireContext()

            viewModel.obtainGoogleToken(context)?.let {
                authenticateWithGoogle(it)
                return@launch
            }

            Timber.d("Could not obtain Google account, starting Sign-In")
            withContext(Dispatchers.Main) {
                startActivityForResult(viewModel.getGoogleSignInIntent(context), CODE_SIGN_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 0) {
            Timber.d("Google Sign-In dialog was dismissed")
            fallbackToAnonymousAuthentication(null)
            return
        }

        if (requestCode == CODE_SIGN_IN) {
            launch {
                try {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
                    authenticateWithGoogle(account.idToken ?: error("Token not set"))
                } catch (e: ApiException) {
                    Timber.e(e, "Google sign-in failed")
                    fallbackToAnonymousAuthentication(e.message)
                } catch (e: Throwable) {
                    Timber.e(e)
                    throw e
                }
            }
        }
    }

    private fun fallbackToAnonymousAuthentication(message: String?) {
        AlertDialog.Builder(requireContext())
            .setMessage(message ?: getString(R.string.google_sign_in_error))
            .setNeutralButton(android.R.string.ok, null)
            .setOnDismissListener {
                launch {
                    if (viewModel.authenticateAnonymously()) {
                        notifyDependentsThatUserIsAuthenticated()
                    } else {
                        withContext(Dispatchers.Main) { toast("Authentication failed.") }
                    }
                }
            }.show()
    }

    private suspend fun notifyDependentsThatUserIsAuthenticated() {
        val userId = viewModel.getUserId()

        Reporter.setUserId(userId)
        withContext(Dispatchers.Main) { listener.onAuthenticated(userId) }
    }
}