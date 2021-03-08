package cz.frantisekmasa.wfrp_master.core.ui.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FlowRow
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing

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

        FlowRow(verticalSpacing = 8.dp, horizontalSpacing = 8.dp) {
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
    val color = if (selected) MaterialTheme.colors.primary else LocalContentColor.current

    Surface(
        border = if (selected) BorderStroke(1.dp, color.copy(alpha = 0.25f)) else null,
        shape = RoundedCornerShape(Spacing.small),
        color = if (selected) color.copy(alpha = 0.15f) else color.copy(alpha = 0.1f),
    ) {
        val fontStyle = MaterialTheme.typography.body2.copy(color = color)

        Text(
            text,
            style = fontStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable(onClick = onClick)
                /* TODO: REMOVE COMMENT */
                .widthIn(min = with(LocalDensity.current) { fontStyle.fontSize.toDp() * 3 })
                .padding(Spacing.small),
        )
    }
}