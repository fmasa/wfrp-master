package cz.frantisekmasa.wfrp_master.desktop

import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import cz.frantisekmasa.wfrp_master.common.appModule
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.config.Platform
import cz.frantisekmasa.wfrp_master.common.core.config.StaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalFileChooserFactory
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalFileSaverFactory
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalUrlOpener
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.ProvideNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.ScreenWithBreakpoints
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.KeyboardDispatcher
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalKeyboardDispatcher
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.Theme
import cz.frantisekmasa.wfrp_master.common.localization.FixedStrings
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import cz.frantisekmasa.wfrp_master.common.shell.DrawerShell
import cz.frantisekmasa.wfrp_master.common.shell.SnackbarScaffold
import cz.frantisekmasa.wfrp_master.desktop.interop.DesktopUrlOpener
import cz.frantisekmasa.wfrp_master.desktop.interop.NativeFileChooser
import cz.frantisekmasa.wfrp_master.desktop.interop.NativeFileSaver
import kotlinx.coroutines.launch
import org.kodein.di.compose.withDI

@ExperimentalMaterialApi
object WfrpMasterApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        val keyboardDispatcher = KeyboardDispatcher()

        application {
            withDI(appModule) {
                val coroutineScope = rememberCoroutineScope()

                CompositionLocalProvider(
                    LocalUrlOpener provides DesktopUrlOpener,
                    LocalFileChooserFactory provides { NativeFileChooser(coroutineScope, it) },
                    LocalFileSaverFactory provides { NativeFileSaver(coroutineScope, it) },
                    LocalStaticConfiguration provides StaticConfiguration(
                        isProduction = true,
                        version = "dev",
                        platform = Platform.Desktop,
                    ),
                    LocalKeyboardDispatcher provides keyboardDispatcher,
                ) {
                    Window(
                        title = FixedStrings.appName,
                        onCloseRequest = ::exitApplication,
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

                                        Navigator(
                                            screens = listOf(PartyListScreen),
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
                                                        Shortcuts()
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
        }
    }
}
