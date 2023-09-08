package cz.frantisekmasa.wfrp_master.common.shell

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.PersistentSnackbarHolder

@Composable
fun SnackbarScaffold(content: @Composable () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val persistentSnackbarHolder =
        remember(coroutineScope, snackbarHostState) {
            PersistentSnackbarHolder(coroutineScope, snackbarHostState)
        }

    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        content = {
            CompositionLocalProvider(
                LocalPersistentSnackbarHolder provides persistentSnackbarHolder,
                content = content,
            )
        },
    )
}
