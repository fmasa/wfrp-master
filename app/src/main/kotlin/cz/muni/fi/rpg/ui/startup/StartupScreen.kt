package cz.muni.fi.rpg.ui.startup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import cz.frantisekmasa.wfrp_master.common.auth.AuthenticationManager
import cz.frantisekmasa.wfrp_master.common.auth.LocalWebClientId
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.core.shared.edit
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.settings.AppSettings
import cz.frantisekmasa.wfrp_master.common.shell.SplashScreen
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun StartupScreen(authenticationManager: AuthenticationManager) {
    SplashScreen()

    val authenticated by authenticationManager.authenticated.collectWithLifecycle()

    Napier.d("Authenticated: $authenticated")

    if (authenticated != false) {
        // We could not determine whether user is logged in yet or there is delay between
        // recompositions (user is already authenticated, but startup screen is still visible)
        return
    }

    val coroutineScope = rememberCoroutineScope()

    var showAnonymousAuthenticationDialog by rememberSaveable { mutableStateOf(false) }

    if (showAnonymousAuthenticationDialog) {
        val settings: SettingsStorage by localDI().instance()

        AnonymousAuthenticationExplanationDialog(
            onDismissRequest = {
                coroutineScope.launch {
                    settings.edit(AppSettings.GOOGLE_SIGN_IN_DISMISSED, true)
                    authenticationManager.authenticateAnonymously()
                }
                showAnonymousAuthenticationDialog = false
            }
        )
    }

    val context = LocalContext.current
    val webClientId = LocalWebClientId.current
    val contract = remember(authenticationManager, webClientId) { authenticationManager.googleSignInContract(webClientId) }
    val googleSignInLauncher = key(contract, coroutineScope) {
        rememberLauncherForActivityResult(contract) { result ->
            if (result.resultCode == 0) {
                Napier.d("Google Sign-In dialog was dismissed")
                showAnonymousAuthenticationDialog = true
                return@rememberLauncherForActivityResult
            }

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    GoogleSignIn.getSignedInAccountFromIntent(result.intent)
                        .await()
                        .idToken
                        ?.let { idToken -> authenticationManager.signInWithGoogleToken(idToken) }
                } catch (e: Throwable) {
                    showAnonymousAuthenticationDialog = true
                }
            }
        }
    }

    LaunchedEffect(null) {
        withContext(Dispatchers.Default) {
            // If user signed in via Google before, try to sign in him directly
            if (authenticationManager.attemptToRestoreExistingGoogleSignIn(context, webClientId)) {
                return@withContext
            }

            withContext(Dispatchers.Main) { googleSignInLauncher.launch(GoogleSignInCode) }
        }
    }
}

@Composable
private fun AnonymousAuthenticationExplanationDialog(onDismissRequest: () -> Unit) {
    val strings = LocalStrings.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = { Text(strings.authentication.startupGoogleSignInFailed) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(strings.commonUi.buttonOk.uppercase())
            }
        }
    )
}

private const val GoogleSignInCode = 100
