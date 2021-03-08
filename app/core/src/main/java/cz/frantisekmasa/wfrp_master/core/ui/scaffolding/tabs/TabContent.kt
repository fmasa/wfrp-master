package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Stable
class PagerState(
    private val coroutineScope: CoroutineScope,
    internal val swipeableState: SwipeableState<Int>,
    internal val screenWidth: Int,
    private val screenCount: Int,
) {
    val selectedScreen: Int
        get() = scrollOffset / screenWidth


    val scrolledPercentage: Float
        get() = (scrollOffset % screenWidth).toFloat() / screenWidth

    val scrollOffset by derivedStateOf {
        val offset = swipeableState.offset.value

        if (offset.isNaN()) 0 else offset.toInt()
    }

    val anchors = (0 until screenCount).map { it * screenWidth.toFloat() to it }.toMap()

    fun selectScreen(index: Int) {
        if (index !in 0 until screenCount) {
            return
        }

        coroutineScope.launch {
            swipeableState.animateTo(index)
        }
    }
}

@Composable
fun rememberPagerState(
    screenWidth: Int,
    screenCount: Int,
): PagerState {
    val swipeableState = key(screenWidth, screenCount) { rememberSwipeableState(0) }
    val coroutineScope = rememberCoroutineScope()

    return remember(coroutineScope, swipeableState) {
        PagerState(coroutineScope, swipeableState, screenWidth, screenCount)
    }
}

@Composable
fun <T> TabContent(
    item: T,
    state: PagerState,
    screens: Array<TabScreen<T>>,
    modifier: Modifier = Modifier,
) {
    Pager(
        modifier = modifier,
        state = state,
        screens = remember(screens) { screens.map { { it.content(item) } } },
    )
}

@Composable
private fun Pager(
    state: PagerState,
    screens: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
) {
    val screenWidth = state.screenWidth

    val visibleBounds by derivedStateOf { state.scrollOffset until state.scrollOffset + screenWidth }

    val visibleScreens by derivedStateOf {
        screens.indices.filter {
            val start = it * screenWidth
            val end = (it + 1) * screenWidth - 1

            start in visibleBounds || end in visibleBounds
        }
    }

    SubcomposeLayout(
        modifier
            .clipToBounds()
            .padding(0.dp)
            .background(MaterialTheme.colors.background)
            .swipeable(
                orientation = Orientation.Horizontal,
                state = state.swipeableState,
                anchors = state.anchors,
                reverseDirection = true,
            ),
    ) { constraints ->
        val pagerScreens = visibleScreens.foldRight(emptyList<PagerScreen>()) { screen, previous ->
            previous + PagerScreen(screen, subcompose(screen, screens[screen]).first())
        }

        layout(width = screenWidth, height = constraints.maxHeight) {
            pagerScreens.forEach {
                it.measurable
                    .measure(constraints)
                    .place(it.index * screenWidth - state.scrollOffset, 0)
            }
        }
    }
}

private class PagerScreen(
    val index: Int,
    val measurable: Measurable,
)