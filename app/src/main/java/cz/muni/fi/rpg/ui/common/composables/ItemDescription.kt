package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ItemDescription(
    text: String,
    modifier: Modifier = Modifier,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text,
        style = MaterialTheme.typography.body2,
        modifier = modifier.drawOpacity(0.54f),
        overflow = overflow,
        maxLines = 1,
    )
}