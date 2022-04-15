package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.runtime.Composable

@Composable
actual fun AlertDialog(
    onDismissRequest: () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
) {
    androidx.compose.material.AlertDialog(
        text = text,
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
    )
}