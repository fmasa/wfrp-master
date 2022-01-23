package cz.frantisekmasa.wfrp_master.common.core.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun SettingsTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = Spacing.large, top = Spacing.bodyPadding),
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight.Bold,
    )
}
