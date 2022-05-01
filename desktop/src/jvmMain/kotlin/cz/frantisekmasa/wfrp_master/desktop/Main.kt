package cz.frantisekmasa.wfrp_master.desktop

import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cz.frantisekmasa.wfrp_master.common.appModule
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.config.Platform
import cz.frantisekmasa.wfrp_master.common.core.config.StaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalEmailInitiator
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalFileChooserFactory
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalUrlOpener
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.ScreenWithBreakpoints
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.Theme
import cz.frantisekmasa.wfrp_master.common.partyList.PartyListScreen
import cz.frantisekmasa.wfrp_master.common.shell.DrawerShell
import cz.frantisekmasa.wfrp_master.common.shell.rememberDrawerState
import cz.frantisekmasa.wfrp_master.desktop.interop.DesktopEmailInitiator
import cz.frantisekmasa.wfrp_master.desktop.interop.DesktopUrlOpener
import cz.frantisekmasa.wfrp_master.desktop.interop.NativeFileChooser
import kotlinx.coroutines.launch
import org.kodein.di.compose.withDI


@ExperimentalMaterialApi
fun main() {
    application {
        withDI(appModule) {
            val coroutineScope = rememberCoroutineScope()

            CompositionLocalProvider(
                LocalUrlOpener provides DesktopUrlOpener,
                LocalEmailInitiator provides DesktopEmailInitiator,
                LocalFileChooserFactory provides { NativeFileChooser(coroutineScope, it) },
                LocalStaticConfiguration provides StaticConfiguration(
                    isProduction = true,
                    version = "dev",
                    platform = Platform.Desktop,
                )
            ) {
                Window(onCloseRequest = ::exitApplication) {
                    Theme {
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

//        val idToken =
//            "eyJhbGciOiJSUzI1NiIsImtpZCI6ImQ2M2RiZTczYWFkODhjODU0ZGUwZDhkNmMwMTRjMzZkYzI1YzQyOTIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI3MjMxNTE4OTM5MDYtbTVnYmoyZjhwY2kzc3BnZXY4OXNxa3YyZmg3a2tpN2guYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI3MjMxNTE4OTM5MDYtamNwYTU0aDl1dmRrNjE0cGo4MmFwNTNtNGQ0am1jZDQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDIxMjE3ODQ5NzIxNzU4MTU1NTEiLCJlbWFpbCI6ImZyYW50aXNla21hc2ExQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiRnJhbnRpxaFlayBNYcWhYSIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS0vQU9oMTRHaFpFUXR2aTQwQzlEb1M4S0ZjUkdmd3FtRzdxRDdwWlg2QnBWSU5ES1k9czk2LWMiLCJnaXZlbl9uYW1lIjoiRnJhbnRpxaFlayIsImZhbWlseV9uYW1lIjoiTWHFoWEiLCJsb2NhbGUiOiJlbiIsImlhdCI6MTY0NzE5MTk3MSwiZXhwIjoxNjQ3MTk1NTcxfQ.RcM25by_0ZkLaBhMAtOCyWH_dlmlCzzv3BVSOzQzC1AHliMAgsoOCVnnZZFSnsD11cUhf4HdTZg8VV2PXTvLIjhw-B8iSgkBNI9x53mWXWJCY02bjl1TLh6EaSzUnNZOLlzg0Vkrl5UOho7-9HFGvPIufkc4JSKBxX7-9YfUeBtAWuGJU9hzBO0Ume4Fea03S9TMPxHsKnW1fxLHfC8MAGxxxtMiofwS75KVxd9CGShlz76mCLwVQFS1deIVjFQrnPjog1gdLIlnW7IgIrGk6w4YgNgYQCtve9EF7NAzoBzDaB8NJdf1RisY-zQ2rVMy3AMfA1_UbfL7Xln2T4P7nQ"
//        val firestore = FirestoreOptions.newBuilder()
//            .setEmulatorHost("192.168.0.10:8080")
//            .setProjectId("dnd-master-58fca")
//            .setCredentialsProvider {
//                IdTokenCredentials.newBuilder()
//                    .setTargetAudience("wtf")
//                    .setIdTokenProvider { _, _ -> IdToken.create(idToken) }
//                    .build()
//            }.build()
//            .service
//
//        runBlocking {
//            Firestore(firestore)
//                .collection("parties")
//                .document("a489391e-c1d8-4a91-a7d8-87ab813e962f")
//                .snapshots
//                .collect {
//                    println(it)
//                }
//        }
    }
}
