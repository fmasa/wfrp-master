package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun FilterBar(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(elevation = 2.dp) {
        Box(
            modifier = modifier.fillMaxWidth().padding(Spacing.tiny),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
