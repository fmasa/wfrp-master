package cz.muni.fi.rpg.ui.settings

import android.os.Parcelable
import android.widget.Toast
import androidx.activity.compose.registerForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import cz.frantisekmasa.wfrp_master.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.DialogProgress
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.DialogTitle
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.core.ui.primitives.shortToast
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.SettingsViewModel
import cz.muni.fi.rpg.viewModels.provideAuthenticationViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@Composable
fun SignInCard(viewModel: SettingsViewModel, routing: Routing<Route.Settings>) {
    val authViewModel = provideAuthenticationViewModel()

    val contract = remember(authViewModel) { authViewModel.googleSignInContract() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var pendingSingInConfirmation: PendingSingInConfirmation? by rememberSaveable { mutableStateOf(null) }

    pendingSingInConfirmation?.let {
        ConfirmSignInDialog(
            it.idToken,
            viewModel,
            authViewModel,
            routing,
            onDismissRequest = { pendingSingInConfirmation = null },
        )
    }

    val launcher = registerForActivityResult(contract) { result ->
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

                            pendingSingInConfirmation = PendingSingInConfirmation(idToken)
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

    CardContainer(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CardTitle(R.string.title_account)

            val email = LocalUser.current.email
            if (email != null) {
                Text(stringResource(R.string.signed_in_as))
                Text(email)
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
                        Image(
                            painterResource(R.drawable.googleg_standard_color_18),
                            VisualOnlyIconDescription,
                        )
                        Text("Sign-in")
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmSignInDialog(
    idToken: String,
    viewModel: SettingsViewModel,
    authViewModel: AuthenticationViewModel,
    routing: Routing<Route.Settings>,
    onDismissRequest: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val userId = LocalUser.current.id

    var partyNames: List<String>? by rememberSaveable { mutableStateOf(null) }
    var processing by remember { mutableStateOf(false) }

    val loading = partyNames == null || processing

    LaunchedEffect(null) {
        partyNames = withContext(Dispatchers.IO) { viewModel.getPartyNames(userId) }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(Spacing.bodyPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                ) {
                    DialogTitle(stringResource(R.string.title_duplicate_account))

                    if (loading) {
                        DialogProgress()
                    } else {
                        Text(stringResource(R.string.google_account_collision))
                        Text(stringResource(R.string.google_account_collision_line_2))

                        if (partyNames!!.isNotEmpty()) {
                            Column {
                                Text(stringResource(R.string.lose_access_to_parties))
                                Text(partyNames!!.joinToString("\n"), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small, Alignment.End),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.small, end = Spacing.small),
                ) {
                    TextButton(enabled = !loading, onClick = onDismissRequest) {
                        Text(stringResource(R.string.button_cancel).toUpperCase(Locale.current))
                    }

                    val context = LocalContext.current

                    TextButton(
                        enabled = !loading,
                        onClick = {
                            processing = true
                            coroutineScope.launch(Dispatchers.Default) {
                                try {
                                    authViewModel.signInWithGoogleToken(idToken).let { success ->
                                        if (!success) {
                                            shortToast(context, R.string.google_sign_in_failed)
                                        }
                                    }
                                } catch (e: Throwable) {
                                    Timber.e(e)
                                    throw e
                                }

                                withContext(Dispatchers.Main) {
                                    onDismissRequest()
                                    routing.popUpTo(Route.PartyList)
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.button_sign_in).toUpperCase(Locale.current))
                    }
                }
            }
        }
    }
}

@Parcelize
private data class PendingSingInConfirmation(val idToken: String) : Parcelable

private const val CODE_GOOGLE_SIGN_IN = 1
