package cz.muni.fi.rpg.ui.startup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.registerForActivityResult
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.ui.shell.splashBackground
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.provideSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun StartupScreen(viewModel: AuthenticationViewModel) {
    SplashScreen()

    val authenticated = viewModel.authenticated.collectAsState().value

    Timber.d("Authenticated: $authenticated")

    if (authenticated != false) {
        // We could not determine whether user is logged in yet or there is delay between
        // recompositions (user is already authenticated, but startup screen is still visible)
        return
    }

    val coroutineScope = rememberCoroutineScope()

    var showAnonymousAuthenticationDialog by rememberSaveable { mutableStateOf(false) }

    if (showAnonymousAuthenticationDialog) {
        val settings = provideSettingsViewModel()

        AnonymousAuthenticationExplanationDialog(onDismissRequest = {
            settings.userDismissedGoogleSignIn()
            coroutineScope.launch { viewModel.authenticateAnonymously() }
            showAnonymousAuthenticationDialog = false
        })

    }

    val context = LocalContext.current
    val contract = remember(viewModel) { viewModel.googleSignInContract() }
    val googleSignInLauncher by registerForActivityResult(
        keys = listOf(contract, coroutineScope),
        contract = contract,
    ) { result ->
        if (result.resultCode == 0) {
            Timber.d("Google Sign-In dialog was dismissed")
            showAnonymousAuthenticationDialog = true
            return@registerForActivityResult
        }

        coroutineScope.launch(Dispatchers.IO) {
            try {
                GoogleSignIn.getSignedInAccountFromIntent(result.intent)
                    .await()
                    .idToken
                    ?.let { idToken -> viewModel.signInWithGoogleToken(idToken) }
            } catch (e: Throwable) {
                showAnonymousAuthenticationDialog = true
            }
        }
    }

    LaunchedEffect(null) {
        withContext(Dispatchers.Default) {
            // If user signed in via Google before, try to sign in him directly
            if (viewModel.attemptToRestoreExistingGoogleSignIn(context)) {
                return@withContext
            }

            withContext(Dispatchers.Main) { googleSignInLauncher.launch(GoogleSignInCode) }
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .splashBackground(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(R.drawable.splash_screen_image),
                stringResource(R.string.icon_application_logo),
                Modifier.size (140.dp)
            )
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.h6,
                color = Theme.fixedColors.splashScreenContent,
            )
        }
    }
}

@Composable
private fun AnonymousAuthenticationExplanationDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Text(stringResource(R.string.google_sign_in_error))
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(android.R.string.ok))
            }
        }
    )
}

private const val GoogleSignInCode = 100