package cz.frantisekmasa.wfrp_master.common.core.ui.text

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun SingleLineTextValue(
    label: String,
    value: AnnotatedString,
) {
    if (value.isBlank()) return

    Row {
        Text(
            "$label:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = Spacing.tiny),
        )
        Text(value)
    }
}

@Composable
fun SingleLineTextValue(
    label: String,
    value: String,
) {
    SingleLineTextValue(label, AnnotatedString(value))
}
