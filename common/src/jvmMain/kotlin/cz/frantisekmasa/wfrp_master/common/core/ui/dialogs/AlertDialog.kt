package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.material.AlertDialog as PlatformAlertDialog

@Composable
actual fun AlertDialog(
    onDismissRequest: () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
) {
    PlatformAlertDialog(
        onDismissRequest = onDismissRequest,
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
    )
}
