package cz.frantisekmasa.wfrp_master.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.auth.JvmAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.shell.SplashScreen
import cz.frantisekmasa.wfrp_master.desktop.auth.AuthenticationScreen
import kotlinx.coroutines.delay
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import kotlin.time.Duration.Companion.seconds

@Composable
fun Startup(content: @Composable () -> Unit) {
    val auth: JvmAuthenticationManager by localDI().instance()

    val user = auth.common.user.collectAsState(null).value
    var authenticationScreenVisible by remember { mutableStateOf(false) }

    if (authenticationScreenVisible && user == null) {
        AuthenticationScreen()
        return
    }

    if (user == null) {
        LaunchedEffect(Unit) {
            delay(3.seconds) // TODO: Somehow check whether user is signed in instead
            authenticationScreenVisible = true
        }
        SplashScreen()
        return
    }

    CompositionLocalProvider(
        LocalUser provides user,
        content = content,
    )
}
