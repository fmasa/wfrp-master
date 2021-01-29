package cz.muni.fi.rpg.ui.shell

import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.navigation.NavHostController
import cz.frantisekmasa.wfrp_master.core.ui.buttons.AmbientHamburgerButtonHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
@ExperimentalMaterialApi
fun DrawerShell(navController: NavHostController, bodyContent: @Composable () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    DrawerBackPressHandler(drawerState)

    ModalDrawerLayoutWithFixedDrawerWidth(
        drawerState = drawerState,
        drawerContent = {
            Column(Modifier.consumeTaps()) {
                AppDrawer(drawerState, navController)
            }
        },
        bodyContent = {
            Providers(
                AmbientHamburgerButtonHandler provides { drawerState.open() },
                content = bodyContent,
            )
        },
    )
}

@Composable
private fun DrawerBackPressHandler(drawerState: DrawerState) {
    val onBackPressedDispatcher = AmbientOnBackPressedDispatcher.current
    val lifecycleOwner = AmbientLifecycleOwner.current
    val callback = remember { CloseDrawerOnBackPressCallback(drawerState) }
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

private fun Modifier.consumeTaps() = composed {
    tapGestureFilter {}
}

private class CloseDrawerOnBackPressCallback(
    var drawerState: DrawerState
) : OnBackPressedCallback(true) {

    override fun handleOnBackPressed() {
        drawerState.close()
    }
}
