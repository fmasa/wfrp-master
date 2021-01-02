package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onDispose
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
    val canDrag = mutableStateOf(true)
    val dragStart = mutableStateOf(0f)
    val previousScroll = mutableStateOf(0f)

    Row(
        modifier = modifier
            .clipToBounds()
            .padding(0.dp)
            .horizontalScroll(scrollState, false)
            .background(MaterialTheme.colors.background)
            .nestedDraggable(
                Orientation.Horizontal,
                reverseDirection = true,
                onDragStarted = { dragStart.value = scrollState.value },
                onDrag = { delta ->
                    previousScroll.value = scrollState.value
                    scrollState.scrollBy(delta)

                    val previousScreenOffset = max(dragStart.value - screenWidth, 0f)
                    val nextScreenOffset =
                        min(dragStart.value + screenWidth, screenWidth * screens.size)

                    if (scrollState.value >= nextScreenOffset) {
                        dragStart.value = nextScreenOffset
                    } else if (scrollState.value <= previousScreenOffset) {
                        dragStart.value = previousScreenOffset
                    }

                    scrollState.value - previousScroll.value
                },
                onDragStopped = { velocity ->
                    canDrag.value = false

                    // If scrollState didn't change, do nothing
                    if (scrollState.value % screenWidth == 0f) {
                        canDrag.value = true
                        return@nestedDraggable
                    }

                    val direction = direction(previousScroll.value, scrollState.value)

                    val relativeVelocity = if (direction == Direction.END)
                        abs(velocity) else
                        abs(velocity) * -1

                    val anchors = listOf(
                        if (scrollState.value < dragStart.value)
                            max(dragStart.value - screenWidth, 0f) else
                            min(dragStart.value + screenWidth, screenWidth * screens.size),
                        dragStart.value,
                    )

                    scrollState.smoothScrollTo(
                        anchors.minByOrNull { abs(it - (scrollState.value + relativeVelocity)) }
                            ?: 0f,
                        onEnd = { _, _ -> canDrag.value = true },
                    )
                }
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
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    interactionState: InteractionState? = null,
    startDragImmediately: Boolean = false,
    canDrag: (androidx.compose.ui.gesture.Direction) -> Boolean = { enabled },
    onDragStarted: (startedPosition: Offset) -> Unit = {},
    onDragStopped: (velocity: Float) -> Unit = {},
    onDrag: Density.(Float) -> Float
): Modifier = composed {
    val density = AmbientDensity.current
    onDispose {
        interactionState?.removeInteraction(Interaction.Dragged)
    }

    scrollGestureFilter(
        scrollCallback = object : ScrollCallback {

            override fun onStart(downPosition: Offset) {
                if (enabled) {
                    interactionState?.addInteraction(Interaction.Dragged)
                    onDragStarted(downPosition)
                }
            }

            override fun onScroll(scrollDistance: Float): Float {
                if (!enabled) return 0f
                val toConsume = if (reverseDirection) scrollDistance * -1 else scrollDistance
                return with(density) { abs(onDrag(toConsume)) }
            }

            override fun onCancel() {
                if (enabled) {
                    interactionState?.removeInteraction(Interaction.Dragged)
                    onDragStopped(0f)
                }
            }

            override fun onStop(velocity: Float) {
                if (enabled) {
                    interactionState?.removeInteraction(Interaction.Dragged)
                    onDragStopped(if (reverseDirection) velocity * -1 else velocity)
                }
            }
        },
        orientation = orientation,
        canDrag = canDrag,
        startDragImmediately = startDragImmediately
    )
}