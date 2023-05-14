package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun DialogTitle(text: String) {
    Text(text, style = MaterialTheme.typography.h6)
}
