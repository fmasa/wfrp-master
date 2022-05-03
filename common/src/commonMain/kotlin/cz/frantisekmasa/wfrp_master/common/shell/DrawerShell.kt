package cz.frantisekmasa.wfrp_master.common.shell

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.LocalHamburgerButtonHandler
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.PersistentSnackbarHolder
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterialApi
fun DrawerShell(drawerState: DrawerState, bodyContent: @Composable (PaddingValues) -> Unit) {
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column {
                AppDrawer(drawerState)
            }
        },
        content = {
            val coroutineScope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            val persistentSnackbarHolder = remember(coroutineScope, snackbarHostState) {
                PersistentSnackbarHolder(coroutineScope, snackbarHostState)
            }

            CompositionLocalProvider(
                LocalHamburgerButtonHandler provides { coroutineScope.launch { drawerState.open() } },
                LocalPersistentSnackbarHolder provides persistentSnackbarHolder,
                content = {
                    Scaffold(
                        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
                        content = bodyContent,
                    )
                },
            )
        },
    )
}