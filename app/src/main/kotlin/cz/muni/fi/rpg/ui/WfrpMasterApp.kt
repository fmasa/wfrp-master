package cz.muni.fi.rpg.ui

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.ProvideNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.ScreenWithBreakpoints
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.Theme
import cz.frantisekmasa.wfrp_master.common.core.ui.viewinterop.LocalActivity
import cz.frantisekmasa.wfrp_master.common.invitation.InvitationLinkScreen
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import cz.frantisekmasa.wfrp_master.common.settings.AppSettings
import cz.frantisekmasa.wfrp_master.common.shell.DrawerShell
import cz.frantisekmasa.wfrp_master.common.shell.NetworkStatusBanner
import cz.frantisekmasa.wfrp_master.common.shell.SnackbarScaffold
import cz.muni.fi.rpg.ui.shell.KeepScreenOn
import cz.muni.fi.rpg.ui.shell.ProvideDIContainer
import cz.muni.fi.rpg.ui.shell.Startup
import kotlinx.coroutines.launch
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@ExperimentalMaterialApi
@Composable
fun WfrpMasterApp() {
    ProvideDIContainer {
        val settings: SettingsStorage by localDI().instance()
        val keepScreenOn = settings.watch(AppSettings.KEEP_SCREEN_ON).collectWithLifecycle(null).value ?: true

        if (keepScreenOn) {
            KeepScreenOn()
        }

        Theme {
            Startup {
                ScreenWithBreakpoints {
                    Column {
                        NetworkStatusBanner()
                        val intent = LocalActivity.current.intent
                        val drawerState = rememberDrawerState(DrawerValue.Closed)
                        val coroutineScope = rememberCoroutineScope()

                        Navigator(
                            screens = rememberInitialScreens(intent?.data),
                            onBackPressed = {
                                if (drawerState.isOpen) {
                                    coroutineScope.launch { drawerState.close() }
                                    return@Navigator false
                                }

                                true
                            },
                        ) { navigator ->
                            SnackbarScaffold {
                                DrawerShell(drawerState) {
                                    SlideTransition(navigator) {
                                        ProvideNavigationTransaction(it) {
                                            it.Content()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberInitialScreens(url: Uri?): List<Screen> {
    return remember(url) {
        listOf(PartyListScreen) +
            when (url) {
                null -> emptyList()
                else -> listOf(InvitationLinkScreen(url.toString()))
            }
    }
}
