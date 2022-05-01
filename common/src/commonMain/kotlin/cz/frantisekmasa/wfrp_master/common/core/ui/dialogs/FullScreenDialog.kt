package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.runtime.Composable

@Composable
expect fun FullScreenDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
)
