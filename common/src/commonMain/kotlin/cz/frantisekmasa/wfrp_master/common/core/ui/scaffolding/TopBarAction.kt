package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenu
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun TopBarAction(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    text: String,
) {
    val colorBase = contentColorFor(MaterialTheme.colors.primarySurface)
    val contentColor =
        colorBase.copy(alpha = if (enabled) ContentAlpha.high else ContentAlpha.medium)

    TextButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Text(text.toUpperCase(Locale.current))
        }
    }
}

@Composable
fun OptionsAction(content: @Composable ColumnScope.() -> Unit) {
    var contextMenuExpanded by remember { mutableStateOf(false) }

    IconAction(
        Icons.Filled.MoreVert,
        LocalStrings.current.commonUi.labelOpenContextMenu,
        onClick = { contextMenuExpanded = true }
    )

    DropdownMenu(
        expanded = contextMenuExpanded, onDismissRequest = { contextMenuExpanded = false },
        content = content,
    )
}

@Composable
fun IconAction(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    tint: Color = contentColorFor(MaterialTheme.colors.primarySurface),
) {
    IconButton(onClick = onClick) {
        Icon(
            icon,
            description,
            tint = tint,
        )
    }
}
