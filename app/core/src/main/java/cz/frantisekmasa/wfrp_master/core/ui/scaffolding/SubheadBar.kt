package cz.frantisekmasa.wfrp_master.core.ui.scaffolding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SubheadBar(text: String) {
    Surface(elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Text(
            text,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
        )
    }
}