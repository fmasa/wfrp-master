package cz.frantisekmasa.wfrp_master.core.ui.primitives

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.LongPressDragObserver
import androidx.compose.ui.gesture.longPressDragGestureFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.math.roundToInt

/**
 * List that allows reordering of items using drag&drop
 *
 * Note: Dragging is started after long press. Because of this, items cannot use clickable() modifier.
 * As workaround you can use tapGestureFilter() - it has worse accesibility (there is no support for labels),
 * but it works.
 *
 * TODO: Figure out solution for use of this modifier. Maybe using clickable(onLongClick=()) internally
 *       instead of longPressDragGestureFilter?
 */
@Composable
fun <T> DraggableListFor(
    items: List<T>,
    onReorder: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 0.dp,
    itemContent: @Composable (index: Int, item: T, isDragged: Boolean) -> Unit
) {
    val dragY = animatedFloat(0f)
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }

    var placeableHeights: List<Int> by remember { mutableStateOf(emptyList()) }

    val itemSpacingPx = with(AmbientDensity.current) { itemSpacing.toIntPx() }

    Layout(
        modifier = modifier,
        content = {
            items.forEachIndexed { index, item ->
                val isDragged = draggedItemIndex == index

                Box(
                    Modifier.scrollFriendlyDraggable(
                        onDrag = { delta ->
                            dragY.snapTo(dragY.value + delta)
                        },
                        onDragStarted = {
                            dragY.snapTo(0f)
                            draggedItemIndex = index
                        },
                        onDragStopped = {
                            val newIndex = newItemIndex(
                                draggedItemIndex = draggedItemIndex!!,
                                draggedItemYCoordinate = calculateYCoordinateOfDraggedItem(
                                    dragY,
                                    placeableHeights,
                                    draggedItemIndex,
                                )!!,
                                placeableHeights,
                                itemSpacingPx
                            )

                            if (index != newIndex) {
                                onReorder(items.moveItem(index, newIndex))
                            }

                            draggedItemIndex = null
                        },
                    )
                ) {
                    itemContent(index, item, isDragged)
                }
            }
        },
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        placeableHeights = placeables.map { it.height }

        val emptySpaceHeight =
            (draggedItemIndex?.let { placeables[it].height } ?: 0) + itemSpacingPx
        val draggedItemYCoordinate = calculateYCoordinateOfDraggedItem(
            dragY,
            placeableHeights,
            draggedItemIndex
        )

        var y = 0

        layout(
            width = constraints.maxWidth,
            height = if (constraints.hasBoundedHeight)
                constraints.maxHeight
            else placeableHeights.sum() + (placeableHeights.size - 1) * itemSpacingPx,
        ) {
            placeables.forEachIndexed { index, placeable ->
                val isDragged = draggedItemIndex == index

                placeable.placeRelative(
                    x = 0,
                    y = when {
                        draggedItemYCoordinate == null -> y
                        index == draggedItemIndex -> draggedItemYCoordinate
                        y < draggedItemYCoordinate && index > draggedItemIndex!! -> y - emptySpaceHeight
                        y > draggedItemYCoordinate && index < draggedItemIndex!! -> y + emptySpaceHeight
                        else -> y
                    },
                    if (isDragged) 2f else 1f
                )

                y += itemSpacingPx + placeable.height
            }
        }
    }
}

private fun newItemIndex(
    draggedItemIndex: Int,
    draggedItemYCoordinate: Int,
    itemHeights: List<Int>,
    itemSpacingPx: Int,
): Int {
    var y = 0

    itemHeights.forEachIndexed { index, height ->
        if (y >= draggedItemYCoordinate) {
            return@newItemIndex if (index > draggedItemIndex) index - 1 else index
        }

        y += height + itemSpacingPx
    }

    return itemHeights.lastIndex

}

private fun calculateYCoordinateOfDraggedItem(
    draggedItemYOffset: AnimatedFloat,
    placeableHeights: List<Int>,
    draggedItemIndex: Int?,
): Int? {
    if (draggedItemIndex == null) {
        return null
    }

    return draggedItemYOffset.value.roundToInt() + placeableHeights.take(draggedItemIndex).sum()
}

private fun <T> List<T>.moveItem(sourceIndex: Int, targetIndex: Int): List<T> {
    return toMutableList()
        .apply {
            if (sourceIndex <= targetIndex) {
                Collections.rotate(subList(sourceIndex, targetIndex + 1), -1)
            } else {
                Collections.rotate(subList(targetIndex, sourceIndex + 1), 1)
            }
        }
}

private fun Modifier.scrollFriendlyDraggable(
    onDragStarted: () -> Unit = {},
    onDragStopped: () -> Unit = {},
    onDrag: Density.(Float) -> Unit
) = composed {
    val density = AmbientDensity.current

    longPressDragGestureFilter(
        DragCallback(
            onDragStarted = onDragStarted,
            onDragStopped = onDragStopped,
            onDrag = onDrag,
            density = density,
        )
    )
}


private class DragCallback(
    private val onDragStarted: () -> Unit = {},
    private val onDragStopped: () -> Unit = {},
    private val onDrag: Density.(Float) -> Unit,
    private val density: Density,
) : LongPressDragObserver {

    override fun onLongPress(pxPosition: Offset) {
        onDragStarted()
    }

    override fun onDrag(dragDistance: Offset): Offset {
        with(density) { onDrag(dragDistance.y) }

        return dragDistance
    }

    override fun onCancel() {
        onDragStopped()
    }

    override fun onStop(velocity: Offset) {
        onDragStopped()
    }
}