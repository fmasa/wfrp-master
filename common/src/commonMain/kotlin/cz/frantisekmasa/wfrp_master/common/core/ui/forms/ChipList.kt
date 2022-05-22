package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
                    selected = itemValue == value,
                    modifier = Modifier.clickable {
                        if (itemValue != value) {
                            onValueChange(itemValue)
                        }
                    },
                ) {
                    Text(itemLabel)
                }
            }
        }
    }
}

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    content: @Composable () -> Unit,
) {
    val color = if (selected) MaterialTheme.colors.primary else LocalContentColor.current

    Chip(
        modifier = modifier,
        border = if (selected) BorderStroke(1.dp, color.copy(alpha = 0.25f)) else null,
        color = if (selected) color.copy(alpha = 0.15f) else color.copy(alpha = 0.1f),
    ) {
        ProvideTextStyle(
            LocalTextStyle.current.copy(color = color),
            content = content,
        )
    }
}

@Composable
fun Chip(
    color: Color = LocalContentColor.current.copy(alpha = 0.1f),
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    padding: Dp = Spacing.small,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        border = border,
        shape = RoundedCornerShape(Spacing.small),
        color = color,
    ) {
        val textStyle = MaterialTheme.typography.body2.copy(
            textAlign = TextAlign.Center,
            color = LocalContentColor.current
        )

        Box(
            Modifier.widthIn(
                min = with(LocalDensity.current) { textStyle.fontSize.toDp() * 3 }
            ).padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            ProvideTextStyle(textStyle, content = content)
        }
    }
}
