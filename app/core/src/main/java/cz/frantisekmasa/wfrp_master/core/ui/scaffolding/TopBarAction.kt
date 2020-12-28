package cz.frantisekmasa.wfrp_master.core.ui.scaffolding

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier

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
