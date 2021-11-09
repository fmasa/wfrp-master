package cz.frantisekmasa.wfrp_master.core.ui.primitives

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R

@Composable
fun NumberPicker(
    value: Int,
    modifier: Modifier = Modifier,
    label: String? = null,
    color: Color = MaterialTheme.colors.onSurface,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(vertical = 4.dp)
            .then(modifier)
    ) {
        if (label != null) {
            Text(label, style = MaterialTheme.typography.subtitle1)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)

            IconButton(
                onClick = onDecrement,
                modifier = Modifier.size(width = 48.dp, height = 24.dp), /* TODO: REMOVE COMMENT */
            ) {
                Icon(
                    painterResource(R.drawable.ic_remove_circle),
                    contentDescription = stringResource(R.string.icon_decrement),
                    tint = tint,
                )
            }

            Text(
                value.toString(),
                style = MaterialTheme.typography.h5,
                color = color,
            )

            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(width = 48.dp, height = 24.dp), /* TODO: REMOVE COMMENT */
            ) {
                Icon(
                    painterResource(R.drawable.ic_add_circle),
                    contentDescription = stringResource(R.string.icon_increment),
                    tint = tint,
                )
            }
        }
    }
}
