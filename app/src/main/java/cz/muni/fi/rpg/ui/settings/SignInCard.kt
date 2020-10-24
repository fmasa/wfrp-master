package cz.muni.fi.rpg.ui.settings

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.ui.common.composables.CardTitle
import cz.muni.fi.rpg.ui.common.composables.activity
import cz.muni.fi.rpg.ui.common.composables.viewModel
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import cz.muni.fi.rpg.viewModels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun SignInCard(viewModel: SettingsViewModel) {

    val authViewModel: AuthenticationViewModel by viewModel()
    val email = savedInstanceState { authViewModel.getEmail() }

    val activity = activity()

    val contract = GoogleSignInContract(authViewModel)
    val context = ContextAmbient.current
    val coroutineScope = rememberCoroutineScope()

    lateinit var launcher: ActivityResultLauncher<Int?>
    onCommit {
        launcher = activity.registerForActivityResult(contract) { result ->
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    GoogleSignIn.getSignedInAccountFromIntent(result.intent)
                        .await()
                        .idToken?.let { idToken ->
                            try {
                                authViewModel.linkAccountToGoogle(idToken)
                            } catch (e: FirebaseAuthUserCollisionException) {
                                Timber.d(
                                    e,
                                    "Account \"${e.email}\" is already associated with another account"
                                )
                                withContext(Dispatchers.Main) {
                                    coroutineScope.showSignInConfirmationDialog(
                                        context,
                                        email,
                                        authViewModel,
                                        viewModel,
                                        idToken
                                    )
                                }
                            }
                        }
                } catch (e: Throwable) {
                    Timber.e(e, "Google sign-in failed")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Google authentication failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    onDispose {
        launcher.unregister()
    }

    CardContainer(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CardTitle(R.string.title_account)

            val emailValue = email.value
            if (emailValue != null) {
                Text(stringResource(R.string.signed_in_as))
                Text(emailValue)
            } else {
                Text(
                    stringResource(R.string.not_signed_in_description),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedButton(onClick = { launcher.launch(CODE_GOOGLE_SIGN_IN) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(imageResource(R.drawable.googleg_standard_color_18))
                        Text("Sign-in")
                    }
                }
            }
        }
    }
}

private fun CoroutineScope.showSignInConfirmationDialog(
    context: Context,
    googleEmail: MutableState<String?>,
    authViewModel: AuthenticationViewModel,
    viewModel: SettingsViewModel,
    idToken: String
) {
    val view = LayoutInflater.from(context).inflate(R.layout.sign_in_confirmation_dialog, null)

    val dialog = AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.title_duplicate_account))
        .setView(view)
        .setPositiveButton(context.getString(R.string.button_sign_in), null)
        .setNegativeButton(R.string.button_cancel, null)
        .create()

    dialog.setOnShowListener {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            it.isEnabled = false
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
            view.findViewById<View>(R.id.mainView).toggleVisibility(false)
            view.findViewById<View>(R.id.progress).toggleVisibility(true)

            launch(Dispatchers.Default) {
                try {
                    authViewModel.signInWithGoogleToken(idToken).let { success ->
                        withContext(Dispatchers.Main) {
                            if (success) {
                                googleEmail.value = authViewModel.getEmail()
                            } else {
                                Toast.makeText(context, "Google sign-in failed", Toast.LENGTH_SHORT)
                                    .show()
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

private data class Result(
    val resultCode: Int,
    val intent: Intent?,
)

private class GoogleSignInContract(
    private val authViewModel: AuthenticationViewModel
) : ActivityResultContract<Int?, Result>() {

    override fun createIntent(context: Context, requestCode: Int?) =
        authViewModel.getGoogleSignInIntent(context)

    override fun parseResult(resultCode: Int, intent: Intent?): Result {
        Timber.d(resultCode.toString())
        return Result(resultCode, intent)
    }
}

private const val CODE_GOOGLE_SIGN_IN = 1
