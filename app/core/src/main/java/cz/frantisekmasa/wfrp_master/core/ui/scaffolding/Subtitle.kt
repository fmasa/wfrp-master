package cz.frantisekmasa.wfrp_master.core.ui.scaffolding

import androidx.compose.material.AmbientContentColor
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun Subtitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.caption,
        color = AmbientContentColor.current.copy(alpha = ContentAlpha.medium),
    )
}