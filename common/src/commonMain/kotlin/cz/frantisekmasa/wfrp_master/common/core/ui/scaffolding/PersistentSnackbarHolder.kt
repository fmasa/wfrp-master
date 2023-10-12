package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
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

    fun showSnackbar(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: SnackbarAction? = null
    ) {
        coroutineScope.launch(Dispatchers.Default) {
            val result = snackbarHostState.showSnackbar(
                message = message,
                duration = duration,
                actionLabel = action?.label,
            )

            if (result == SnackbarResult.ActionPerformed && action != null) {
                action.onPerform()
            }
        }
    }

    data class SnackbarAction(
        val label: String,
        val onPerform: () -> Unit,
    )
}
