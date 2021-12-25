package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.annotation.DrawableRes
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun CardItem(
    name: String,
    description: String = "",
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    contextMenuItems: List<ContextMenu.Item>,
    badge: @Composable () -> Unit = {},
) {
    WithContextMenu(
        items = contextMenuItems,
        onClick = onClick,
    ) {
        ListItem(
            icon = icon,
            text = { Text(name) },
            secondaryText = if (description.isNotBlank()) ({ Text(description, maxLines = 1) }) else null,
            trailing = badge,
        )
    }

    Divider()
}