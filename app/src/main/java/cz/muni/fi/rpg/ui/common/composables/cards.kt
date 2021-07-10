package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.primitives.VisualOnlyIconDescription

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
                painterResource(iconRes),
                VisualOnlyIconDescription,
                Modifier.padding(end = 4.dp)
                    .width(24.dp)
                    .height(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
            )
        }
        Text(text, style = MaterialTheme.typography.h6, textAlign = TextAlign.Center)
    }
}
