package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Surface(modifier = modifier, elevation = 2.dp) {
        Row(
            Modifier.padding(horizontal = Spacing.large, vertical = Spacing.large),
            content = content,
        )
    }
}
