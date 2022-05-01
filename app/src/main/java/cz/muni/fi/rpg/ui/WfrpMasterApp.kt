package cz.muni.fi.rpg.ui

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.ScreenWithBreakpoints
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.Theme
import cz.frantisekmasa.wfrp_master.common.core.ui.viewinterop.LocalActivity
import cz.frantisekmasa.wfrp_master.common.invitation.InvitationLinkScreen
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import cz.frantisekmasa.wfrp_master.common.shell.DrawerShell
import cz.frantisekmasa.wfrp_master.common.shell.NetworkStatusBanner
import cz.frantisekmasa.wfrp_master.common.shell.rememberDrawerState
import cz.muni.fi.rpg.ui.shell.ProvideDIContainer
import cz.muni.fi.rpg.ui.shell.Startup
import io.ktor.http.Url
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun WfrpMasterApp() {
    ProvideDIContainer {
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
                            }
                        ) {
                            DrawerShell(drawerState) {
                                CurrentScreen()
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
                else -> listOf(InvitationLinkScreen(Url(url.toString())))
            }
    }
}