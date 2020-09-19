package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.weight
import androidx.compose.material.*
import androidx.compose.material.TabRow as MaterialTabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


fun Modifier.animatedTabIndicatorOffset(
    tabPositions: List<TabPosition>,
    originTabIndex: Int,
    scrolledPercentage: Float,
): Modifier = composed {
    val originTab = tabPositions[originTabIndex]

    val currentTabWidth = originTab.width
    val nextTabWidth = tabPositions.getOrElse(originTabIndex + 1) { originTab }.width
    val widthDiff = nextTabWidth - currentTabWidth

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = originTab.left + originTab.width * scrolledPercentage)
        .preferredWidth(currentTabWidth + widthDiff * scrolledPercentage)
}

data class TabScreen<T>(
    @StringRes internal val tabName: Int,
    internal val content: @Composable (T) -> Unit
)

@Composable
fun <T> TabRow(
    screens: Array<TabScreen<T>>,
    scrollState: ScrollState,
    screenWidth: Float,
    fullWidthTabs: Boolean = false
) {
    val selectedTabIndex = (scrollState.value / screenWidth).toInt()
    val scrolledPercentage = ((scrollState.value % screenWidth) / screenWidth)

    val tabs = @Composable {
        val tabModifier = if (fullWidthTabs) Modifier.weight(1f) else Modifier

        screens.forEachIndexed { index, screen ->
            Tab(
                modifier = tabModifier,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = EmphasisAmbient.current.medium.applyEmphasis(
                    MaterialTheme.colors.onSurface
                ),
                selected = index == (if (scrolledPercentage > 0.5f) selectedTabIndex + 1 else selectedTabIndex),
                onClick = { scrollState.smoothScrollTo(index * screenWidth) },
                text = { Text(stringResource(screen.tabName).toUpperCase(Locale.getDefault())) },
            )
        }
    }

    val animatedSelectedTabIndex =
        if (scrolledPercentage > 0.5f) selectedTabIndex + 1 else selectedTabIndex

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        TabConstants.DefaultIndicator(
            Modifier.animatedTabIndicatorOffset(
                tabPositions,
                selectedTabIndex,
                scrolledPercentage
            ),
            color = MaterialTheme.colors.primary
        )
    }

    if (fullWidthTabs) {
        MaterialTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = animatedSelectedTabIndex,
            backgroundColor = MaterialTheme.colors.surface,
            indicator = indicator,
            tabs = tabs,
        )
    } else {
        ScrollableTabRow(
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = animatedSelectedTabIndex,
            backgroundColor = MaterialTheme.colors.surface,
            indicator = indicator,
            tabs = tabs,
        )
    }

}

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
            .draggable(
                Orientation.Horizontal,
                reverseDirection = true,
                onDragStarted = { dragStart.value = scrollState.value },
                onDrag = {
                    previousScroll.value = scrollState.value
                    scrollState.scrollBy(it)

                    val previousScreenOffset = max(dragStart.value - screenWidth, 0f)
                    val nextScreenOffset =
                        min(dragStart.value + screenWidth, screenWidth * screens.size)

                    if (scrollState.value >= nextScreenOffset) {
                        dragStart.value = nextScreenOffset
                    } else if (scrollState.value <= previousScreenOffset) {
                        dragStart.value = previousScreenOffset
                    }
                },
                onDragStopped = { velocity ->
                    canDrag.value = false

                    // If scrollState didn't change, do nothing
                    if (scrollState.value % screenWidth == 0f) {
                        return@draggable
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
