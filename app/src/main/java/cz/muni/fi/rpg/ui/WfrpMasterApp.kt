package cz.muni.fi.rpg.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.ui.shell.DrawerShell
import cz.muni.fi.rpg.ui.shell.ProvideUser

@ExperimentalMaterialApi
@Composable
fun WfrpMasterApp(adManager: AdManager) {
    val navController = rememberNavController()

    Theme {
        ProvideUser {
            DrawerShell(navController) {
                NavGraph(navController, adManager)
            }
        }
    }
}