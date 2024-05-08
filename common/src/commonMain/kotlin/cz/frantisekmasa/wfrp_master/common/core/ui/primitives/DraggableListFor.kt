package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.utils.moveItem
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * List that allows reordering of items using drag&drop
 *
 * Note: Dragging is started after long press. Because of this, items cannot use clickable() modifier.
 * As workaround you can use tapGestureFilter() - it has worse accesibility (there is no support for labels),
 * but it works.
 */
@Composable
fun <T> DraggableListFor(
    items: List<T>,
    onReorder: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 0.dp,
    itemContent: @Composable (index: Int, item: T, isDragged: Boolean) -> Unit,
) {
    val dragY = remember { Animatable(0f) }
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var placeableHeights: List<Int> by remember { mutableStateOf(emptyList()) }

    val itemSpacingPx = with(LocalDensity.current) { itemSpacing.roundToPx() }
    val coroutineScope = rememberCoroutineScope()
    Layout(
        modifier = modifier,
        content = {
            items.forEachIndexed { index, item ->
                Box(
                    Modifier.pointerInput(placeableHeights, itemSpacingPx, coroutineScope, items) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                coroutineScope.launch { dragY.snapTo(0f) }
                                draggedItemIndex = index
                            },
                            onDragEnd = {
                                val newIndex =
                                    newItemIndex(
                                        draggedItemIndex = draggedItemIndex!!,
                                        draggedItemYCoordinate =
                                            calculateYCoordinateOfDraggedItem(
                                                dragY,
                                                placeableHeights,
                                                draggedItemIndex,
                                            )!!,
                                        placeableHeights,
                                        itemSpacingPx,
                                    )

                                if (index != newIndex) {
                                    onReorder(items.moveItem(index, newIndex))
                                }

                                draggedItemIndex = null
                            },
                            onDrag = { change, delta ->
                                coroutineScope.launch {
                                    dragY.snapTo(dragY.value + delta.y)
                                    change.consume()
                                }
                            },
                            onDragCancel = { draggedItemIndex = null },
                        )
                    },
                ) {
                    itemContent(index, item, draggedItemIndex == index)
                }
            }
        },
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        placeableHeights = placeables.map { it.height }

        val emptySpaceHeight =
            (draggedItemIndex?.let { placeables[it].height } ?: 0) + itemSpacingPx
        val draggedItemYCoordinate =
            calculateYCoordinateOfDraggedItem(
                dragY,
                placeableHeights,
                draggedItemIndex,
            )

        var y = 0

        layout(
            width = constraints.maxWidth,
            height =
                if (constraints.hasBoundedHeight) {
                    constraints.maxHeight
                } else {
                    placeableHeights.sum() + (placeableHeights.size - 1) * itemSpacingPx
                },
        ) {
            placeables.forEachIndexed { index, placeable ->
                val isDragged = draggedItemIndex == index

                placeable.placeRelative(
                    x = 0,
                    y =
                        when {
                            draggedItemYCoordinate == null -> y
                            index == draggedItemIndex -> draggedItemYCoordinate
                            y < draggedItemYCoordinate && index > draggedItemIndex!! -> y - emptySpaceHeight
                            y > draggedItemYCoordinate && index < draggedItemIndex!! -> y + emptySpaceHeight
                            else -> y
                        },
                    if (isDragged) 2f else 1f,
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
    draggedItemYOffset: Animatable<Float, AnimationVector1D>,
    placeableHeights: List<Int>,
    draggedItemIndex: Int?,
): Int? {
    if (draggedItemIndex == null) {
        return null
    }

    return draggedItemYOffset.value.roundToInt() + placeableHeights.take(draggedItemIndex).sum()
}
