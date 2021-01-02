package cz.frantisekmasa.wfrp_master.core.ui.primitives

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    bodyPadding: PaddingValues = PaddingValues(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(Modifier.padding(vertical = 6.dp).then(modifier)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                Modifier.padding(vertical = 16.dp, horizontal = 8.dp).padding(bodyPadding),
                content = content
            )
        }
    }
}

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
fun MultiLineTextValue(@StringRes labelRes: Int, value: String) {
    if (value.isBlank()) return

    Column {
        Text(stringResource(labelRes), fontWeight = FontWeight.Bold)
        Text(value)
    }
}

@Composable
fun SingleLineTextValue(@StringRes labelRes: Int, value: String) {
    if (value.isBlank()) return

    Row {
        Text(
            stringResource(labelRes) + ":",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(value)
    }
}