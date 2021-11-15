package cz.muni.fi.rpg.ui.shell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.viewModel.providePremiumViewModel
import cz.frantisekmasa.wfrp_master.common.core.viewModel.provideSettingsViewModel
import cz.muni.fi.rpg.ui.startup.StartupScreen
import cz.muni.fi.rpg.viewModels.provideAuthenticationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Startup(content: @Composable () -> Unit) {
    val auth = provideAuthenticationViewModel()
    val settings = provideSettingsViewModel()
    val premiumViewModel = providePremiumViewModel()

    val user = auth.user.collectWithLifecycle().value
    val premiumActive = premiumViewModel.active
    var adsInitialized by rememberSaveable { mutableStateOf(false) }

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

    CompositionLocalProvider(LocalUser provides user, content = content)
}
