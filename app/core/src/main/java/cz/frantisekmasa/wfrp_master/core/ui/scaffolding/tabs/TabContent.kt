package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.ScrollCallback
import androidx.compose.ui.gesture.scrollGestureFilter
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


@Composable
fun <T> TabContent(
    item: T,
    screens: Array<TabScreen<T>>,
    scrollState: ScrollState,
    screenWidth: Float,
    modifier: Modifier = Modifier,
) {
    var dragStart by remember { mutableStateOf(0f) }
    var previousScroll by remember { mutableStateOf(0f) }

    // We want to ignore drags from small area next to the start of the screen
    // to let user open the navigation drawer.
    val ignoredDragOffset = with(AmbientDensity.current) { 10.dp.toPx() }

    Row(
        modifier = modifier
            .clipToBounds()
            .padding(0.dp)
            .horizontalScroll(scrollState, false)
            .background(MaterialTheme.colors.background)
            .nestedDraggable(
                Orientation.Horizontal,
                onDragStarted = { dragStart = scrollState.value },
                onDrag = { delta ->
                    previousScroll = scrollState.value
                    scrollState.scrollBy(delta)

                    val previousScreenOffset = max(dragStart - screenWidth, 0f)
                    val nextScreenOffset =
                        min(dragStart + screenWidth, screenWidth * screens.size)

                    if (scrollState.value >= nextScreenOffset) {
                        dragStart = nextScreenOffset
                    } else if (scrollState.value <= previousScreenOffset) {
                        dragStart = previousScreenOffset
                    }

                    scrollState.value - previousScroll
                },
                onDragStopped = { velocity ->
                    // If scrollState didn't change, do nothing
                    if (scrollState.value % screenWidth == 0f) {
                        return@nestedDraggable
                    }

                    val direction = direction(previousScroll, scrollState.value)

                    val relativeVelocity = if (direction == Direction.END)
                        abs(velocity) else
                        abs(velocity) * -1

                    val anchors = listOf(
                        if (scrollState.value < dragStart)
                            max(dragStart - screenWidth, 0f) else
                            min(dragStart + screenWidth, screenWidth * screens.size),
                        dragStart,
                    )

                    scrollState.smoothScrollTo(
                        anchors.minByOrNull { abs(it - (scrollState.value + relativeVelocity)) }
                            ?: 0f
                    )
                },
                canStartDragging = { it.x % screenWidth > ignoredDragOffset },
            ),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        screens.forEach { it.content(item) }
    }
}

private fun direction(from: Float, to: Float) = if (from < to) Direction.END else Direction.START

private enum class Direction {
    START,
    END,
}


fun Modifier.nestedDraggable(
    orientation: Orientation,
    onDragStarted: (startedPosition: Offset) -> Unit = {},
    onDragStopped: (velocity: Float) -> Unit = {},
    onDrag: Density.(Float) -> Float,
    canStartDragging: (startedPosition: Offset) -> Boolean,
): Modifier = composed {
    val density = AmbientDensity.current

    scrollGestureFilter(
        orientation = orientation,
        scrollCallback = object : ScrollCallback {
            private var dragging: Boolean = false

            override fun onStart(downPosition: Offset) {
                if (canStartDragging(downPosition)) {
                    dragging = true
                    onDragStarted(downPosition)
                }
            }

            override fun onScroll(scrollDistance: Float): Float {
                return if (dragging)
                    with(density) { abs(onDrag(-scrollDistance)) } else
                    0f
            }

            override fun onCancel() {
                onDragStopped(0f)
                dragging = false
            }

            override fun onStop(velocity: Float) {
                onDragStopped(-velocity)
                dragging = false
            }
        },
    )
}