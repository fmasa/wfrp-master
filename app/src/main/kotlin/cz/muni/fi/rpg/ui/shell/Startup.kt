package cz.muni.fi.rpg.ui.shell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cz.frantisekmasa.wfrp_master.common.auth.AndroidAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.muni.fi.rpg.ui.startup.StartupScreen
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun Startup(content: @Composable () -> Unit) {
    val auth: AndroidAuthenticationManager by localDI().instance()
    val user = auth.common.user.collectWithLifecycle().value

    if (user == null) {
        StartupScreen(auth)
        return
    }

    CompositionLocalProvider(LocalUser provides user, content = content)
}
