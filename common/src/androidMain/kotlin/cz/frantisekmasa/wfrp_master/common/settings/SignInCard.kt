package cz.frantisekmasa.wfrp_master.common.settings

import android.os.Parcelable
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.auth.AuthenticationManager
import cz.frantisekmasa.wfrp_master.common.auth.LocalWebClientId
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
actual fun SignInCard(settingsScreenModel: SettingsScreenModel) {
    val authManager: AuthenticationManager by localDI().instance()
    val webClientId = LocalWebClientId.current
    val contract = remember(authManager) { authManager.googleSignInContract(webClientId) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var pendingSingInConfirmation: PendingSingInConfirmation? by rememberSaveable { mutableStateOf(null) }

    pendingSingInConfirmation?.let {
        ConfirmSignInDialog(
            authManager,
            it.idToken,
            settingsScreenModel,
            onDismissRequest = { pendingSingInConfirmation = null },
        )
    }

    val launcher = rememberLauncherForActivityResult(contract) { result ->
        coroutineScope.launch(Dispatchers.IO) {
            try {
                GoogleSignIn.getSignedInAccountFromIntent(result.intent)
                    .await()
                    .idToken?.let { idToken ->
                        try {
                            authManager.linkAccountToGoogle(idToken)
                        } catch (e: FirebaseAuthUserCollisionException) {
                            Napier.d(
                                "Account \"${e.email}\" is already associated with another account",
                                e,
                            )

                            pendingSingInConfirmation = PendingSingInConfirmation(idToken)
                        }
                    }
            } catch (e: Throwable) {
                Napier.e("Google sign-in failed", e)
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
            CardTitle(stringResource(Str.settings_title_account))

            val email = LocalUser.current.email

            if (email != null) {
                Text(stringResource(Str.authentication_messages_signed_in_as))
                Text(email)
            } else {
                Text(
                    stringResource(Str.authentication_messages_not_signed_in_description),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedButton(onClick = { launcher.launch(CODE_GOOGLE_SIGN_IN) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            drawableResource(Resources.Drawable.GoogleLogo),
                            VisualOnlyIconDescription,
                        )
                        Text(stringResource(Str.authentication_button_sign_in))
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmSignInDialog(
    authManager: AuthenticationManager,
    idToken: String,
    screenModel: SettingsScreenModel,
    onDismissRequest: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val userId = LocalUser.current.id

    var partyNames: List<String>? by rememberSaveable { mutableStateOf(null) }
    var processing by remember { mutableStateOf(false) }

    val loading = partyNames == null || processing

    LaunchedEffect(null) {
        partyNames = withContext(Dispatchers.IO) { screenModel.getPartyNames(userId) }
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
                    DialogTitle(stringResource(Str.authentication_messages_duplicate_account))

                    if (loading) {
                        DialogProgress()
                    } else {
                        Text(stringResource(Str.authentication_messages_google_account_collision))

                        if (partyNames!!.isNotEmpty()) {
                            Column {
                                Text(
                                    stringResource(
                                        Str.authentication_messages_lose_access_to_parties
                                    )
                                )
                                Text(
                                    partyNames!!.joinToString("\n"),
                                    fontWeight = FontWeight.SemiBold,
                                )
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
                        Text(stringResource(Str.common_ui_button_cancel).uppercase())
                    }

                    val snackbarHolder = LocalPersistentSnackbarHolder.current
                    val navigation = LocalNavigationTransaction.current

                    val googleSignInFailedMessage = stringResource(
                        Str.messages_authentication_google_sign_in_failed
                    )
                    TextButton(
                        enabled = !loading,
                        onClick = {
                            processing = true
                            coroutineScope.launch(Dispatchers.Default) {
                                try {
                                    authManager.signInWithGoogleToken(idToken).let { success ->
                                        if (!success) {
                                            snackbarHolder.showSnackbar(
                                                googleSignInFailedMessage,
                                                SnackbarDuration.Short,
                                            )
                                        }
                                    }
                                } catch (e: Throwable) {
                                    Napier.e(e.toString(), e)
                                    throw e
                                }

                                withContext(Dispatchers.Main) {
                                    onDismissRequest()
                                    navigation.goBackTo { it is PartyListScreen }
                                }
                            }
                        }
                    ) {
                        Text(stringResource(Str.authentication_button_sign_in).uppercase())
                    }
                }
            }
        }
    }
}

@Parcelize
private data class PendingSingInConfirmation(val idToken: String) : Parcelable

private const val CODE_GOOGLE_SIGN_IN = 1
