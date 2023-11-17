package cz.frantisekmasa.wfrp_master.desktop

import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.ProvideNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.ScreenWithBreakpoints
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.KeyboardDispatcher
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalKeyboardDispatcher
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.Theme
import cz.frantisekmasa.wfrp_master.common.localization.FixedStrings
import cz.frantisekmasa.wfrp_master.common.shell.DrawerShell
import cz.frantisekmasa.wfrp_master.common.shell.SnackbarScaffold
import kotlinx.coroutines.launch
import java.util.UUID

@Immutable
data class ApplicationWindowState(
    val initialScreen: Screen,
    val key: UUID,
    val isPrimary: Boolean,
)

@Composable
fun ApplicationWindow(
    initialScreen: Screen,
    onCloseRequest: () -> Unit,
    onNewWindowRequest: (initialScreen: Screen) -> Unit,
) {
    val keyboardDispatcher = remember { KeyboardDispatcher() }

    CompositionLocalProvider(
        LocalKeyboardDispatcher provides keyboardDispatcher,
    ) {
        Window(
            title = FixedStrings.appName,
            onCloseRequest = onCloseRequest,
            onPreviewKeyEvent = {
                keyboardDispatcher.dispatch(it, beforeChildren = true)
            },
            onKeyEvent = {
                keyboardDispatcher.dispatch(it, beforeChildren = false)
            },
        ) {
            Theme {
                SnackbarScaffold {
                    Startup {
                        ScreenWithBreakpoints {
                            val drawerState = rememberDrawerState(DrawerValue.Closed)
                            val coroutineScope = rememberCoroutineScope()

                            Navigator(
                                screens = listOf(initialScreen),
                                onBackPressed = {
                                    if (drawerState.isOpen) {
                                        coroutineScope.launch { drawerState.close() }
                                        return@Navigator false
                                    }

                                    true
                                }
                            ) { navigator ->
                                DrawerShell(drawerState) {
                                    val screen = navigator.lastItem

                                    navigator.saveableState("currentScreen") {
                                        ProvideNavigationTransaction(screen) {
                                            Shortcuts(
                                                onNewWindowRequest = onNewWindowRequest,
                                            )
                                            screen.Content()
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
