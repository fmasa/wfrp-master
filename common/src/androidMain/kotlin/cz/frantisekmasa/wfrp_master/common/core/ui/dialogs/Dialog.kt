package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.runtime.Composable

@Composable
actual fun Dialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest,
        content = content,
    )
}