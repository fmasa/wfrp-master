package cz.muni.fi.rpg.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import cz.muni.fi.rpg.viewModels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingsFragment : BaseFragment(R.layout.fragment_settings),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        const val CODE_GOOGLE_SIGN_IN = 1
    }

    private val viewModel: SettingsViewModel by viewModel()
    private val authViewModel: AuthenticationViewModel by viewModel()
    private val googleEmail by lazy { MutableLiveData<String?>(authViewModel.getEmail()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        googleEmail.observe(viewLifecycleOwner) { email ->
            if (email != null) {
                Timber.d("EMAIL: $email")
                view.findViewById<TextView>(R.id.userEmail).text = email
            }

            val loggedIn = email != null

            view.findViewById<View>(R.id.userLoggedIn).toggleVisibility(loggedIn)
            view.findViewById<View>(R.id.userEmail).toggleVisibility(loggedIn)
            view.findViewById<View>(R.id.userNotLoggedIn).toggleVisibility(!loggedIn)
            view.findViewById<View>(R.id.signInButton).toggleVisibility(!loggedIn)
            view.findViewById<View>(R.id.accountCard).toggleVisibility(true)
        }

        view.findViewById<View>(R.id.signInButton).setOnClickListener {
            startActivityForResult(
                authViewModel.getGoogleSignInIntent(requireContext()),
                CODE_GOOGLE_SIGN_IN
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 0) {
            Timber.d("Google Sign-In dialog was dismissed")
            return
        }

        if (requestCode == CODE_GOOGLE_SIGN_IN) {
            launch {
                try {
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                        .await()
                        .idToken?.let {
                            try {
                                authViewModel.linkAccountToGoogle(it)
                            } catch (e: FirebaseAuthUserCollisionException) {
                                Timber.d(
                                    e,
                                    "Account \"${e.email}\" is already associated with another account"
                                )
                                withContext(Dispatchers.Main) {
                                    showSignInConfirmationDialog(it)
                                }
                            }
                        }
                } catch (e: Throwable) {
                    Timber.e(e, "Google sign-in failed")
                    withContext(Dispatchers.Main) { toast("Google authentication failed") }
                }
            }
        }
    }

    private fun showSignInConfirmationDialog(idToken: String) {
        val view = layoutInflater.inflate(R.layout.sign_in_confirmation_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.title_duplicate_account))
            .setView(view)
            .setPositiveButton(getString(R.string.button_sign_in), null)
            .setNegativeButton(R.string.button_cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                it.isEnabled = false
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
                view.findViewById<View>(R.id.mainView).toggleVisibility(false)
                view.findViewById<View>(R.id.progress).toggleVisibility(true)

                launch {
                    try {
                        authViewModel.signInWithGoogleToken(idToken).let { success ->
                            withContext(Dispatchers.Main) {
                                if (success) {
                                    googleEmail.value = authViewModel.getEmail()
                                } else {
                                    toast("Google sign-in failed")
                                }
                            }
                        }
                    } catch (e: Throwable) {
                        Timber.e(e)
                        throw e
                    }

                    withContext(Dispatchers.Main) { dialog.dismiss() }
                }
            }
        }

        dialog.setCanceledOnTouchOutside(false);
        dialog.show()

        launch {
            val partyNames = viewModel.getPartyNames(authViewModel.getUserId())

            withContext(Dispatchers.Main) {
                val parties: TextView = view.findViewById(R.id.parties)
                if (partyNames.isEmpty()) {
                    view.findViewById<View>(R.id.loseAccessToParties).toggleVisibility(false)
                    parties.toggleVisibility(false)
                }

                parties.text = partyNames.joinToString("\n")

                view.findViewById<View>(R.id.mainView).toggleVisibility(true)
                view.findViewById<View>(R.id.progress).toggleVisibility(false)
            }
        }
    }
}