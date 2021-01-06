package cz.frantisekmasa.wfrp_master.core.ui.primitives

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.R

@Composable
fun NumberPicker(
    modifier: Modifier = Modifier,
    value: Int,
    label: String? = null,
    color: Color = MaterialTheme.colors.onSurface,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 4.dp).then(modifier)
    ) {
        if (label != null) {
            Text(label, style = MaterialTheme.typography.subtitle1)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconColorFilter =
                ColorFilter.tint(MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))

            Image(
                modifier = Modifier.clickable(onClick = onDecrement),
                imageVector = vectorResource(R.drawable.ic_remove_circle),
                colorFilter = iconColorFilter,
            )

            Text(
                value.toString(),
                style = MaterialTheme.typography.h5,
                color = color,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Image(
                modifier = Modifier.clickable(onClick = onIncrement),
                imageVector = vectorResource(R.drawable.ic_add_circle),
                colorFilter = iconColorFilter,
            )
        }
    }
}