package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt


@Stable
class PagerState(
    private val coroutineScope: CoroutineScope,
    internal val scrollState: ScrollState,
    internal val swipeableState: SwipeableState<Int>,
    private val screenWidth: Int,
    private val screenCount: Int,
) {
    val selectedScreen: Int
        get() = scrollState.value / screenWidth


    val scrolledPercentage: Float
        get() = (scrollState.value % screenWidth).toFloat() / screenWidth

    val anchors = (0..screenCount).map { it * screenWidth.toFloat() to it }.toMap()

    fun selectScreen(index: Int) {
        if (index !in 0 until screenCount) {
            return
        }

        coroutineScope.launch {
            scrollState.animateScrollTo(index * screenWidth)
        }
    }
}

@Composable
fun rememberPagerState(
    screenWidth: Float,
    screenCount: Int,
    ignoredDragOffset: Dp = 10.dp,
): PagerState {
    val scrollState = key(screenWidth, screenCount) { rememberScrollState(0) }
    val swipeableState = key(screenWidth, screenCount) { rememberSwipeableState(0) }
    val coroutineScope = rememberCoroutineScope()

    val offset = swipeableState.offset.value

    DisposableEffect(offset) {
        if (!offset.isNaN()) {
            coroutineScope.launch {
                scrollState.scrollTo(offset.roundToInt())
            }
        }
        onDispose {  }
    }

    return remember(scrollState, ignoredDragOffset, coroutineScope) {
        PagerState(coroutineScope, scrollState, swipeableState, screenWidth.roundToInt(), screenCount)
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
            .background(MaterialTheme.colors.background)
            .horizontalScroll(state.scrollState, enabled = false)
            .swipeable(
                orientation = Orientation.Horizontal,
                state = state.swipeableState,
                anchors = state.anchors,
                reverseDirection = true,
            ),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        screens.forEach { it.content(item) }
    }
}
