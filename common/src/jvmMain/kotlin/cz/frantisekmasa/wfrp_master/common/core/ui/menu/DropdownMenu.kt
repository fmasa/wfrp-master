package cz.frantisekmasa.wfrp_master.common.core.ui.menu

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.DropdownMenu as PlatformDropdownMenu
import androidx.compose.material.DropdownMenuItem as PlatformDropdownMenuItem

@Composable
actual fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    PlatformDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        content = content,
    )
}

@Composable
actual fun DropdownMenuItem(
    onClick: () -> Unit,
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit
) {
    PlatformDropdownMenuItem(
        onClick = onClick,
        modifier = modifier,
        content = content,
    )
}
