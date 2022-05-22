package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import kotlin.math.max

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    verticalSpacing: Dp,
    horizontalSpacing: Dp,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val rows = mutableListOf<Row>()
        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()

        placeables.forEach { placeable ->
            if (rows.isEmpty() || !rows.last().hasRoomFor(placeable)) {
                val lastRow = rows.lastOrNull()

                rows.add(
                    Row(
                        spacing = horizontalSpacingPx,
                        maxWidth = constraints.maxWidth,
                        y = if (lastRow != null)
                            lastRow.y + lastRow.height + verticalSpacingPx
                        else 0,
                        firstChild = placeable,
                    )
                )
            } else {
                rows.last().addChild(placeable)
            }
        }

        layout(
            width = rows.maxOfOrNull { it.width } ?: 0,
            height = rows.lastOrNull()?.let { it.y + it.height } ?: 0,
        ) {
            rows.forEach { it.placeTo(this@layout) }
        }
    }
}

private class Row(
    private val spacing: Int,
    private val maxWidth: Int,
    val y: Int,
    firstChild: Placeable,
) {
    var width = firstChild.width
        private set

    var height = firstChild.height
        private set

    private val children = mutableListOf(firstChild)

    fun hasRoomFor(placeable: Placeable) = width + spacing + placeable.width <= maxWidth

    fun addChild(placeable: Placeable) {
        children += placeable
        width += spacing + placeable.width
        height = max(height, placeable.height)
    }

    fun placeTo(scope: Placeable.PlacementScope) {
        with(scope) {
            var x = 0
            children.forEach { child ->
                child.place(x, y)
                x += spacing + child.width
            }
        }
    }
}
