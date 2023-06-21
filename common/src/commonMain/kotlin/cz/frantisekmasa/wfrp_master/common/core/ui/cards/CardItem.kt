package cz.frantisekmasa.wfrp_master.common.core.ui.cards

import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu

@Composable
fun CardItem(
    name: String,
    description: String = "",
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    contextMenuItems: List<ContextMenu.Item> = emptyList(),
    badge: @Composable () -> Unit = {},
    showDivider: Boolean = true,
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

    if (showDivider) {
        Divider()
    }
}
