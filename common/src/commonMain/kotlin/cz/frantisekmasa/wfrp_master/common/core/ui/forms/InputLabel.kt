package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun InputLabel(label: String, bottomPadding: Dp = 4.dp) {
    Text(
        label,
        style = MaterialTheme.typography.body2,
        maxLines = 1,
        modifier = Modifier.padding(bottom = bottomPadding),
    )
}

@Composable
fun SelectBoxLabel(label: String) {
    InputLabel(label, bottomPadding = 2.dp)
}
