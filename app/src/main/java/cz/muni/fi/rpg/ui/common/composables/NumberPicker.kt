package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.ColorRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R

@Composable
fun NumberPicker(
    value: Int,
    label: String? = null,
    @ColorRes color: Int = R.color.colorText,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalGravity = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 4.dp).then(modifier)
    ) {
        if (label != null) {
            Text(label, style = MaterialTheme.typography.subtitle1)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalGravity = Alignment.CenterVertically
        ) {
            val iconColorFilter = ColorFilter.tint(colorResource(R.color.colorGray))

            Image(
                modifier = Modifier.clickable(onClick = onDecrement),
                asset = vectorResource(R.drawable.ic_remove_circle),
                colorFilter = iconColorFilter,
            )

            Text(
                value.toString(),
                style = MaterialTheme.typography.h5,
                color = colorResource(color),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Image(
                modifier = Modifier.clickable(onClick = onIncrement),
                asset = vectorResource(R.drawable.ic_add_circle),
                colorFilter = iconColorFilter,
            )
        }
    }
}