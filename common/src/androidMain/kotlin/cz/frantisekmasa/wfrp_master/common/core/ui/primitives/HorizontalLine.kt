package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalLine(modifier: Modifier = Modifier) {
    Divider(
        color = MaterialTheme.colors.onBackground.copy(ContentAlpha.disabled),
        thickness = (2f / LocalDensity.current.density).dp,
        modifier = modifier.padding(top = 20.dp)
    )
}
