package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    description: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    contextMenuItems: List<ContextMenu.Item>,
    badgeContent: @Composable () -> Unit
) {
    val menuOpened = mutableStateOf(false)

    Box(paddingBottom = 1.dp) {
        Surface(elevation = 2.dp) {
            Row(
                Modifier
                    .clickable(onClick = onClick)
                    .longPressGestureFilter(onLongPress = { menuOpened.value = true })
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalGravity = Alignment.CenterVertically,
            ) {
                ItemIcon(iconRes, ItemIcon.Size.Small)

                Column(Modifier.weight(1f, fill = true)) {
                    val modifier = Modifier.padding(start = 16.dp)
                    Text(
                        name,
                        modifier = modifier,
                        fontSize = TextUnit.Sp(18),
                        overflow = TextOverflow.Ellipsis
                    )

                    if (description.isNotBlank()) {
                        ItemDescription(
                            description,
                            overflow = TextOverflow.Ellipsis,
                            modifier = modifier,
                        )
                    }
                }

                badgeContent()
            }
        }

        ContextMenu(
            items = contextMenuItems,
            onDismissRequest = { menuOpened.value = false },
            expanded = menuOpened.value
        )
    }
}