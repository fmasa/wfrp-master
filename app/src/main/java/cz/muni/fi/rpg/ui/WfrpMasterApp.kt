package cz.muni.fi.rpg.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.core.ui.components.ScreenWithBreakpoints
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.ui.shell.DrawerShell
import cz.muni.fi.rpg.ui.shell.NetworkStatusBanner
import cz.muni.fi.rpg.ui.shell.Startup
import cz.muni.fi.rpg.ui.shell.rememberNavControllerWithAnalytics

@ExperimentalMaterialApi
@Composable
fun WfrpMasterApp() {
    val navController = rememberNavControllerWithAnalytics()

    Theme {
        Startup {
            ScreenWithBreakpoints {
                Column {
                    NetworkStatusBanner()
                    DrawerShell(navController) {
                        NavGraph(navController)
                    }
                }
            }
        }
    }
}
