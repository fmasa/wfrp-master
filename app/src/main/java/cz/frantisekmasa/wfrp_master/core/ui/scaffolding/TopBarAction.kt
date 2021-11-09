package cz.frantisekmasa.wfrp_master.core.ui.scaffolding

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.muni.fi.rpg.R

@Composable
fun TopBarAction(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    @StringRes textRes: Int,
) {
    val colorBase = contentColorFor(MaterialTheme.colors.primarySurface)
    val contentColor =
        colorBase.copy(alpha = if (enabled) ContentAlpha.high else ContentAlpha.medium)

    TextButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Text(stringResource(textRes).toUpperCase(Locale.current))
        }
    }
}

@Composable
fun OptionsAction(content: @Composable ColumnScope.() -> Unit) {
    var contextMenuExpanded by remember { mutableStateOf(false) }

    IconAction(
        painterResource(R.drawable.ic_more),
        stringResource(R.string.icon_action_more),
        onClick = { contextMenuExpanded = true }
    )

    DropdownMenu(
        expanded = contextMenuExpanded, onDismissRequest = { contextMenuExpanded = false },
        content = content,
    )
}

@Composable
fun IconAction(painter: Painter, description: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(painter, description, tint = contentColorFor(MaterialTheme.colors.primarySurface))
    }
}
