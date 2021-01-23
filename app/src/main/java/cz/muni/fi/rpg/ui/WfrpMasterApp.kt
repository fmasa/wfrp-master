package cz.muni.fi.rpg.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.core.ads.AdManager
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.ui.shell.DrawerShell
import cz.muni.fi.rpg.ui.shell.Startup
import cz.muni.fi.rpg.ui.shell.rememberNavControllerWithAnalytics

@ExperimentalMaterialApi
@Composable
fun WfrpMasterApp(adManager: AdManager) {
    val navController = rememberNavControllerWithAnalytics()

    Theme {
        Startup {
            DrawerShell(navController) {
                NavGraph(navController, adManager)
            }
        }
    }
}