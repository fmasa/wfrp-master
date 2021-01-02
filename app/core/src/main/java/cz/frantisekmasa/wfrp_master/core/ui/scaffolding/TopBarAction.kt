package cz.frantisekmasa.wfrp_master.core.ui.scaffolding

import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase

@Deprecated("Use version that expects only string resource")
@Composable
fun TopBarAction(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val colorBase = contentColorFor(MaterialTheme.colors.primarySurface)
    val contentColor = colorBase.copy(alpha = if (enabled) ContentAlpha.high else ContentAlpha.medium)

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
