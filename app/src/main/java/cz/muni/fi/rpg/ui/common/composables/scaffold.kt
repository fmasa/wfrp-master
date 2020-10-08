package cz.muni.fi.rpg.ui.common.composables

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ContentColorAmbient
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.vectorResource
import androidx.drawerlayout.widget.DrawerLayout
import cz.muni.fi.rpg.R

@Composable
fun TopBarAction(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val colorBase = contentColorFor(MaterialTheme.colors.primarySurface)
    val contentColor = if (enabled)
        colorBase else
        EmphasisAmbient.current.disabled.applyEmphasis(colorBase)

    TextButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        Providers(ContentColorAmbient provides contentColor) { content() }
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) { Icon(vectorResource(R.drawable.ic_navigate_back)) }
}

@Composable
fun HamburgerButton() {
    val context = ContextAmbient.current
    require(context is AppCompatActivity)

    IconButton(onClick = { context.findViewById<DrawerLayout>(R.id.drawer_layout)?.open() }) {
        Icon(vectorResource(R.drawable.ic_menu))
    }
}

@Composable
fun Subtitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.caption,
        color = EmphasisAmbient.current.medium.applyEmphasis(contentColor()),
    )
}