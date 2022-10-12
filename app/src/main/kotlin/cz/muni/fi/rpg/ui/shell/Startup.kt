package cz.muni.fi.rpg.ui.shell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cz.frantisekmasa.wfrp_master.common.auth.LocalAuthenticationManager
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.muni.fi.rpg.ui.startup.StartupScreen

@Composable
fun Startup(content: @Composable () -> Unit) {
    val auth = LocalAuthenticationManager.current
    val user = auth.user.collectWithLifecycle().value

    if (user == null) {
        StartupScreen()
        return
    }

    CompositionLocalProvider(LocalUser provides user, content = content)
}
