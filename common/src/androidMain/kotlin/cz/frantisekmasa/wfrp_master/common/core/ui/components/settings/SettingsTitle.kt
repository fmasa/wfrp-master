package cz.frantisekmasa.wfrp_master.common.core.ui.components.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun SettingsTitle(@StringRes textRes: Int) {
    Text(
        text = stringResource(textRes),
        modifier = Modifier.padding(start = Spacing.large, top = Spacing.bodyPadding),
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight.Bold,
    )
}
