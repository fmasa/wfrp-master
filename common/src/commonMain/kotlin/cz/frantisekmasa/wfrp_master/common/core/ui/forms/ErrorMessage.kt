package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ErrorMessage(message: String, textAlign: TextAlign? = null) {
    Text(
        message,
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.body2,
        textAlign = textAlign,
    )
}
