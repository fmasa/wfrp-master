package cz.muni.fi.rpg.ui.shell

import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.frantisekmasa.wfrp_master.core.auth.AmbientUser
import cz.muni.fi.rpg.ui.startup.StartupScreen
import cz.muni.fi.rpg.viewModels.provideAuthenticationViewModel
import cz.muni.fi.rpg.viewModels.provideSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Startup(content: @Composable () -> Unit) {
    val auth = provideAuthenticationViewModel()
    val settings = provideSettingsViewModel()

    val user = auth.user.collectAsState().value
    var initializationComplete by savedInstanceState { false }

    LaunchedEffect(null) {
        withContext(Dispatchers.IO) {
            settings.initializeAds()
            initializationComplete = true
        }
    }

    if (user == null || !initializationComplete) {
        StartupScreen(auth)
        return
    }

    Providers(AmbientUser provides user, content = content)
}