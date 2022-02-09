package cz.frantisekmasa.wfrp_master.common.core.ui.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription

@Composable
fun CardTitle(text: String, icon: ImageVector? = null) {
    CardTitle(text, icon?.let { rememberVectorPainter(it) })
}

@Composable
private fun CardTitle(text: String, painter: Painter?) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (painter != null) {
            Image(
                painter,
                VisualOnlyIconDescription,
                Modifier.padding(end = 4.dp)
                    .width(24.dp)
                    .height(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
            )
        }
        Text(text, style = MaterialTheme.typography.h6, textAlign = TextAlign.Center)
    }
}
