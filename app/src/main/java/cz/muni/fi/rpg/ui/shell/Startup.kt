package cz.muni.fi.rpg.ui.shell

import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import cz.frantisekmasa.wfrp_master.core.auth.AmbientUser
import cz.frantisekmasa.wfrp_master.core.auth.UserId
import cz.muni.fi.rpg.ui.premium.providePremiumViewModel
import cz.muni.fi.rpg.ui.startup.StartupScreen
import cz.muni.fi.rpg.viewModels.provideAuthenticationViewModel
import cz.muni.fi.rpg.viewModels.provideSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Startup(content: @Composable () -> Unit) {
    val auth = provideAuthenticationViewModel()
    val settings = provideSettingsViewModel()
    val premiumViewModel = providePremiumViewModel()

    val user = auth.user.collectAsState().value
    val premiumActive = premiumViewModel.active
    var adsInitialized by savedInstanceState { false }

    if (user != null) {
        LaunchedEffect("premium") {
            withContext(Dispatchers.IO) {
                premiumViewModel.refreshPremiumForUser(UserId.fromString(user.id))
            }
        }
    }

    LaunchedEffect(null) {
        withContext(Dispatchers.IO) {
            settings.initializeAds()
            adsInitialized = true
        }
    }

    if (user == null || !adsInitialized || premiumActive == null) {
        StartupScreen(auth)
        return
    }

    Providers(AmbientUser provides user, content = content)
}