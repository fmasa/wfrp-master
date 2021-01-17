package cz.frantisekmasa.wfrp_master.core.ui.scaffolding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing

@Composable
fun SubheadBar(text: String) {
    SubheadBar {
        Text(
            text,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun SubheadBar(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(elevation = 2.dp) {
        Box(
            modifier = modifier.fillMaxWidth().padding(Spacing.large),
            contentAlignment = Alignment.Center,
        ) {
           content()
        }
    }
}