package cz.frantisekmasa.wfrp_master.core.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DialogProgress() {
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
