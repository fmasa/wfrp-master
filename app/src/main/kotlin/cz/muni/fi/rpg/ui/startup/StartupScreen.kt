package cz.muni.fi.rpg.ui.startup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.auth.AndroidAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.auth.LocalWebClientId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.shell.SplashScreen
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun StartupScreen(authenticationManager: AndroidAuthenticationManager): Unit = Box {

    var signInButtonVisible by rememberSaveable { mutableStateOf(false) }

    val authenticated by authenticationManager.common.authenticated.collectWithLifecycle()

    Napier.d("Authenticated: $authenticated")

    if (authenticated != false) {
        SplashScreen()
        // We could not determine whether user is logged in yet or there is delay between
        // recompositions (user is already authenticated, but startup screen is still visible)
        return
    }

    val webClientId = LocalWebClientId.current
    val context = LocalContext.current

    val startGoogleSignInFlow = rememberGoogleSignInLauncher(
        authenticationManager = authenticationManager,
        onFailure = { signInButtonVisible = true },
    )

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            // If user signed in via Google before, try to sign in him directly
            if (authenticationManager.attemptToRestoreExistingGoogleSignIn(context, webClientId)) {
                return@withContext
            }

            withContext(Dispatchers.Main) { startGoogleSignInFlow() }
        }
    }

    if (signInButtonVisible) {
        SplashScreen {
            Button(
                onClick = startGoogleSignInFlow
            ) {
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
    } else {
        SplashScreen()
    }
}

@Composable
private fun rememberGoogleSignInLauncher(
    authenticationManager: AndroidAuthenticationManager,
    onFailure: () -> Unit,
): () -> Unit {
    val webClientId = LocalWebClientId.current
    val contract = remember(authenticationManager, webClientId) { authenticationManager.googleSignInContract(webClientId) }
    val coroutineScope = rememberCoroutineScope()

    return key(contract, coroutineScope) {
        val launcher = rememberLauncherForActivityResult(contract) { result ->
            if (result.resultCode == 0) {
                Napier.d("Google Sign-In dialog was dismissed")
                onFailure()
                return@rememberLauncherForActivityResult
            }

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    GoogleSignIn.getSignedInAccountFromIntent(result.intent)
                        .await()
                        .idToken
                        ?.let { idToken -> authenticationManager.signInWithGoogleToken(idToken) }
                } catch (e: Throwable) {
                    onFailure()
                }
            }
        }

        return@key remember(launcher, onFailure) {
            {
                try {
                    launcher.launch(GoogleSignInCode)
                } catch (e: Throwable) {
                    onFailure()
                }
            }
        }
    }
}

private const val GoogleSignInCode = 100
