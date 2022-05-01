package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Dialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.wrapContentHeight(),
        onDismissRequest = onDismissRequest,
        text = content,
        buttons = {},
    )
}
