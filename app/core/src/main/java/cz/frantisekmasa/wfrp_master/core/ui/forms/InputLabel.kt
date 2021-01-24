package cz.frantisekmasa.wfrp_master.core.ui.forms

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
internal fun InputLabel(label: String) {
    Text(
        label,
        style = MaterialTheme.typography.body2,
        maxLines = 1,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}
