package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.ScrollCallback
import androidx.compose.ui.gesture.scrollGestureFilter
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


// TODO: Render only screens that are visible + fixed amount of screens around
//@Stable
//data class PagerState(
//    private val scrollState: ScrollState,
//    private val screenWidth: Float,
//    private val offscreenLimit: Int,
//    private val screenCount: Int,
//) {
//    val renderedPages: List<Int>
//        get() {
//            val screenCenter = scrollState.value + screenWidth / 2
//            val renderedRange =
//                (screenCenter - screenWidth * offscreenLimit)..(screenCenter + screenWidth * offscreenLimit)
//
//            return (0 until screenCount).filter { x -> (x + screenWidth / 2) in renderedRange }
//        }
//}

@Stable
class PagerState(
    private val coroutineScope: CoroutineScope,
    private val ignoredDragOffset: Float,
    internal val scrollState: ScrollState,
    private val screenWidth: Float,
    private val screenCount: Int,
) {
    private var previousScroll: Float = 0f
    private var dragStart: Float = 0f

    val selectedScreen: Int
        get() = (scrollState.value / screenWidth).toInt()


    val scrolledPercentage: Float
        get() = (scrollState.value % screenWidth) / screenWidth

    fun selectScreen(index: Int) {
        if (index !in 0 until screenCount) {
            return
        }

        coroutineScope.launch { scrollState.smoothScrollTo(index * screenWidth) }
    }

    internal val scrollCallback = object : ScrollCallback {
        private var dragging: Boolean = false

        override fun onStart(downPosition: Offset) {
            if (downPosition.x % screenWidth > ignoredDragOffset) {
                dragging = true
                dragStart = scrollState.value
            }
        }

        override fun onScroll(scrollDistance: Float): Float {
            if (!dragging) {
                return 0f // TODO: Check if we cannot simply consume everything
            }

            coroutineScope.launch {
                previousScroll = scrollState.value
                val newsScrollState = previousScroll - scrollDistance

                scrollState.scrollTo(newsScrollState)

                val previousScreenOffset = max(dragStart - screenWidth, 0f)
                val nextScreenOffset = min(dragStart + screenWidth, screenWidth * screenCount)

                if (newsScrollState >= nextScreenOffset) {
                    dragStart = nextScreenOffset
                } else if (newsScrollState <= previousScreenOffset) {
                    dragStart = previousScreenOffset
                }
            }

            return abs(scrollDistance)
        }

        override fun onCancel() {
            onDragStopped(0f)
            dragging = false
        }

        override fun onStop(velocity: Float) {
            onDragStopped(-velocity)
            dragging = false
        }

        private fun onDragStopped(velocity: Float) {
            coroutineScope.launch {
            // If scrollState didn't change, do nothing
            if (scrollState.value % screenWidth == 0f) {
                return@launch
            }

            val direction = direction(previousScroll, scrollState.value)

            val relativeVelocity = if (direction == Direction.END)
                abs(velocity) else
                abs(velocity) * -1

            val anchors = listOf(
                if (scrollState.value < dragStart)
                    max(dragStart - screenWidth, 0f) else
                    min(dragStart + screenWidth, screenWidth * screenCount),
                dragStart,
            )

            val scrollPosition = scrollState.value + relativeVelocity

                scrollState.smoothScrollTo(
                    anchors.minByOrNull { abs(it - scrollPosition) } ?: 0f
                )
            }
        }
    }
}

@Composable
fun rememberPagerState(
    screenWidth: Float,
    screenCount: Int,
    ignoredDragOffset: Dp = 10.dp,
): PagerState {
    val density = AmbientDensity.current
    val scrollState = key(screenWidth, screenCount) { rememberScrollState(0f) }
    val ignoredDragOffsetPx = remember(ignoredDragOffset) {
        with(density) { ignoredDragOffset.toPx() }
    }

    val coroutineScope = rememberCoroutineScope()

    return remember(scrollState, ignoredDragOffset, coroutineScope) {
        PagerState(coroutineScope, ignoredDragOffsetPx, scrollState, screenWidth, screenCount)
    }
}

@Composable
fun <T> TabContent(
    item: T,
    state: PagerState,
    screens: Array<TabScreen<T>>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clipToBounds()
            .padding(0.dp)
            .horizontalScroll(state.scrollState, false)
            .background(MaterialTheme.colors.background)
            .scrollGestureFilter(
                state.scrollCallback,
                Orientation.Horizontal,
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
