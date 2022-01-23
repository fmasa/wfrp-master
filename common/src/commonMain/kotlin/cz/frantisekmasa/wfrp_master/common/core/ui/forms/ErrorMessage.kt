package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ErrorMessage(message: String) {
    Text(
        message,
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.body2,
    )
}
