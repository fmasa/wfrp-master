package cz.frantisekmasa.wfrp_master.desktop

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.application
import cz.frantisekmasa.wfrp_master.common.appModule
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.config.Platform
import cz.frantisekmasa.wfrp_master.common.core.config.StaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalFileChooserFactory
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalFileSaverFactory
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalUrlOpener
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import cz.frantisekmasa.wfrp_master.desktop.interop.DesktopUrlOpener
import cz.frantisekmasa.wfrp_master.desktop.interop.NativeFileChooser
import cz.frantisekmasa.wfrp_master.desktop.interop.NativeFileSaver
import io.github.aakira.napier.Napier
import org.kodein.di.compose.withDI
import java.util.UUID

@ExperimentalMaterialApi
object WfrpMasterApplication {
    @JvmStatic
    fun main(args: Array<String>) {
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
                ) {
                    val windows = rememberSaveable {
                        mutableStateListOf(
                            ApplicationWindowState(
                                initialScreen = PartyListScreen,
                                key = UUID.randomUUID(),
                                isPrimary = true,
                            )
                        )
                    }

                    windows.forEach { window ->
                        key(window.key) {
                            ApplicationWindow(
                                initialScreen = window.initialScreen,
                                onNewWindowRequest = { initialScreen ->
                                    windows += ApplicationWindowState(
                                        initialScreen = initialScreen,
                                        key = UUID.randomUUID(),
                                        isPrimary = false,
                                    )
                                },
                                onCloseRequest = {
                                    if (window.isPrimary) {
                                        Napier.d("Requested closing of primary window, exiting the app")
                                        exitApplication()
                                    } else {
                                        Napier.d("Closing non-primary window")
                                        windows.removeIf { it.key == window.key }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
