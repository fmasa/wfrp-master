package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp

@Composable
fun CardTitle(@StringRes textRes: Int, @DrawableRes iconRes: Int? = null) {
    CardTitle(stringResource(textRes), iconRes)
}

@Composable
fun CardTitle(text: String, @DrawableRes iconRes: Int? = null) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (iconRes != null) {
            Image(
                vectorResource(iconRes),
                Modifier.padding(end = 4.dp)
                    .width(24.dp)
                    .height(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
            )
        }
        Text(text, style = MaterialTheme.typography.h6, textAlign = TextAlign.Center)
    }
}

@Composable
fun CardButton(@StringRes textRes: Int, onClick: () -> Unit) {
    Box(alignment = Alignment.TopCenter, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        OutlinedButton(onClick = onClick) {
            Text(stringResource(textRes).toUpperCase(Locale.current))
        }
    }
}

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