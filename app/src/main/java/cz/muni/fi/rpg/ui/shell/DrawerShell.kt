package cz.muni.fi.rpg.ui.shell

import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavHostController
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.LocalHamburgerButtonHandler
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.PersistentSnackbarHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
@ExperimentalMaterialApi
fun DrawerShell(navController: NavHostController, bodyContent: @Composable (PaddingValues) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    DrawerBackPressHandler(drawerState)

    ModalDrawerLayoutWithFixedDrawerWidth(
        drawerState = drawerState,
        drawerContent = {
            Column {
                AppDrawer(drawerState, navController)
            }
        },
        bodyContent = {
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

@Composable
private fun DrawerBackPressHandler(drawerState: DrawerState) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcher.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val callback = remember(drawerState, coroutineScope) {
        CloseDrawerOnBackPressCallback(drawerState, coroutineScope)
    }
    DisposableEffect(onBackPressedDispatcher, lifecycleOwner) {
        callback.remove()
        onBackPressedDispatcher.addCallback(lifecycleOwner, callback)

        onDispose {
            callback.remove()
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        withContext(Dispatchers.Main) {
            callback.isEnabled = drawerState.isOpen
        }
    }
}

private class CloseDrawerOnBackPressCallback(
    val drawerState: DrawerState,
    val coroutineScope: CoroutineScope
) : OnBackPressedCallback(true) {

    override fun handleOnBackPressed() {
        coroutineScope.launch { drawerState.close() }
    }
}
