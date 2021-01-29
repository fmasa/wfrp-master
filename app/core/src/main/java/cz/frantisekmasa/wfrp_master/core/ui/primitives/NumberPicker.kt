package cz.frantisekmasa.wfrp_master.core.ui.primitives

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.R

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
                modifier = Modifier.preferredSize(width = 48.dp, height = 24.dp),
            ) {
                Icon(
                    vectorResource(R.drawable.ic_remove_circle),
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
                modifier = Modifier.preferredSize(width = 48.dp, height = 24.dp),
            ) {
                Icon(
                    vectorResource(R.drawable.ic_add_circle),
                    contentDescription = stringResource(R.string.icon_increment),
                    tint = tint,
                )
            }
        }
    }
}