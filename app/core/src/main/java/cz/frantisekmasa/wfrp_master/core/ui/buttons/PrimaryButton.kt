package cz.frantisekmasa.wfrp_master.core.ui.buttons

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp


@Composable
fun PrimaryButton(
    @StringRes textRes: Int,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(stringResource(textRes).toUpperCase(Locale.current))
    }
}