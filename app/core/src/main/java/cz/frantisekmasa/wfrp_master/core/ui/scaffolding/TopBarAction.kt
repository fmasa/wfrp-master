package cz.frantisekmasa.wfrp_master.core.ui.scaffolding

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.frantisekmasa.wfrp_master.core.R

@Deprecated("Use version that expects only string resource")
@Composable
fun TopBarAction(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val colorBase = contentColorFor(MaterialTheme.colors.primarySurface)
    val contentColor =
        colorBase.copy(alpha = if (enabled) ContentAlpha.high else ContentAlpha.medium)

    TextButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        Providers(AmbientContentColor provides contentColor) { content() }
    }
}

@Composable
fun TopBarAction(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    @StringRes textRes: Int,
) {
    TopBarAction(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick
    ) {
        Text(stringResource(textRes).toUpperCase(Locale.current))
    }
}

@Composable
fun OptionsAction(items: @Composable ColumnScope.() -> Unit) {
    var contextMenuExpanded by remember { mutableStateOf(false) }

    DropdownMenu(
        toggle = {
            IconButton(onClick = { contextMenuExpanded = true }) {
                Icon(
                    vectorResource(R.drawable.ic_more),
                    stringResource(R.string.icon_action_more),
                    tint = contentColorFor(MaterialTheme.colors.primarySurface),
                )
            }
        },
        expanded = contextMenuExpanded, onDismissRequest = { contextMenuExpanded = false },
        dropdownContent = items,
    )
}

