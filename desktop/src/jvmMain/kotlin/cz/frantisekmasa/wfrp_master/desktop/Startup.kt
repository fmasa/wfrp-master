package cz.frantisekmasa.wfrp_master.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import cz.frantisekmasa.wfrp_master.common.auth.AuthenticationManager
import cz.frantisekmasa.wfrp_master.common.auth.AuthenticationManager.AuthenticationStatus
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.shell.SplashScreen
import cz.frantisekmasa.wfrp_master.desktop.auth.AuthenticationScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun Startup(content: @Composable () -> Unit) {
    val auth: AuthenticationManager by localDI().instance()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            auth.refreshUser()
        }
    }

    when (val status = auth.status.collectAsState(null).value) {
        null -> SplashScreen()
        is AuthenticationStatus.NotAuthenticated -> AuthenticationScreen()
        is AuthenticationStatus.Authenticated -> CompositionLocalProvider(
            LocalUser provides status.user,
            content = content
        )
    }
}

