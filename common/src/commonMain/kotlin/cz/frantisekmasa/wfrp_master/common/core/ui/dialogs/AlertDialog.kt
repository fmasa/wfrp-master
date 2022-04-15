package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.runtime.Composable

@Composable
expect fun AlertDialog(
    onDismissRequest: () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
)