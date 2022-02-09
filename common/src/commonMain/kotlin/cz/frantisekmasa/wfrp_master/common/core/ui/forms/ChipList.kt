package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FlowRow
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

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
fun Chip(
    selected: Boolean = false,
    content: @Composable (Color) -> Unit,
) {
    val color = if (selected) MaterialTheme.colors.primary else LocalContentColor.current

    Surface(
        border = if (selected) BorderStroke(1.dp, color.copy(alpha = 0.25f)) else null,
        shape = RoundedCornerShape(Spacing.small),
        color = if (selected) color.copy(alpha = 0.15f) else color.copy(alpha = 0.1f),
    ) {
        content(color)
    }
}

@Composable
fun Chip(
    text: String,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false
) {
    Chip(
        selected = selected,
    ) { color ->
        val fontStyle = MaterialTheme.typography.body2.copy(color = color)

        val modifier = if (onClick != null)
            Modifier.clickable(onClick = onClick)
        else Modifier

        Text(
            text,
            style = fontStyle,
            textAlign = TextAlign.Center,
            modifier = modifier
                .widthIn(min = with(LocalDensity.current) { fontStyle.fontSize.toDp() * 3 })
                .padding(Spacing.small),
        )
    }
}
