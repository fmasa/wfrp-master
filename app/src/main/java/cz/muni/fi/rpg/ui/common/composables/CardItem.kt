package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun CardItem(
    name: String,
    description: String = "",
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    contextMenuItems: List<ContextMenu.Item>,
    badgeContent: @Composable () -> Unit = {}
) {
    val menuOpened = mutableStateOf(false)

    ListItem(
        modifier = Modifier
            .clickable(onClick = onClick)
            .longPressGestureFilter { menuOpened.value = true },
        icon = { ItemIcon(iconRes, ItemIcon.Size.Small) },
        text = { Text(name) },
        secondaryText = if (description.isNotBlank()) ({ Text(description) }) else null,
        trailing = badgeContent,
    )

    Divider()

    ContextMenu(
        items = contextMenuItems,
        onDismissRequest = { menuOpened.value = false },
        expanded = menuOpened.value
    )
}