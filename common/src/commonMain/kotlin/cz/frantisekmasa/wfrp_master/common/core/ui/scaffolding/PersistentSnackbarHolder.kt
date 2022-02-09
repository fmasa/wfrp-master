package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val LocalPersistentSnackbarHolder = staticCompositionLocalOf<PersistentSnackbarHolder> {
    error("LocalSnackbarHostState was not initialized")
}

@Stable
class PersistentSnackbarHolder(
    private val coroutineScope: CoroutineScope,
    private val snackbarHostState: SnackbarHostState,
) {

    fun showSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        coroutineScope.launch(Dispatchers.Default) {
            snackbarHostState.showSnackbar(message = message, duration = duration)
        }
    }
}