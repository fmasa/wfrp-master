package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun Subtitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.caption,
        color = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
    )
}
