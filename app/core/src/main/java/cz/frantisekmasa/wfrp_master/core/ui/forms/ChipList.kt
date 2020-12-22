package cz.frantisekmasa.wfrp_master.core.ui.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp

@Composable
fun <T> ChipList(
    items: Iterable<Pair<T, String>>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    Column(modifier) {
        label?.let {
            InputLabel(it)
        }

        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
            for ((itemValue, itemLabel) in items) {
                Chip(
                    text = itemLabel,
                    selected = itemValue == value,
                    onClick = {
                        if (itemValue != value) {
                            onValueChange(itemValue)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun Chip(
    text: String,
    onClick: () -> Unit,
    selected: Boolean = false
) {
    val textStyle = MaterialTheme.typography.body2
    val padding = 8.dp
    val borderRadius = with(AmbientDensity.current) { textStyle.fontSize.toDp() / 2 + padding }
    val color = if (selected) MaterialTheme.colors.primary else AmbientContentColor.current

    Surface(
        shape = RoundedCornerShape(size = borderRadius),
        color = if (selected) color.copy(alpha = 0.15f) else color.copy(alpha = 0.1f),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.body2.copy(color = color),
            modifier = Modifier.clickable(onClick = onClick).padding(padding),
        )
    }
}