package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.launch

@Composable
fun TabPager(
    modifier: Modifier = Modifier,
    fullWidthTabs: Boolean = false,
    initialPage: Int = 0,
    onPageChange: ((Int) -> Unit)? = null,
    beyondBoundsPageCount: Int = 0,
    content: TabPagerScope.() -> Unit
) {
    Column(modifier) {
        val scope = TabCollector()
        scope.apply(content)

        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { scope.tabNames.size },
        )

        if (onPageChange !== null) {
            val currentPage = pagerState.currentPage

            LaunchedEffect(onPageChange, currentPage) {
                onPageChange(currentPage)
            }
        }

        val indicator = @Composable { tabPositions: List<TabPosition> ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                color = MaterialTheme.colors.primary,
            )
        }

        val tabs = @Composable {
            val coroutineScope = rememberCoroutineScope()

            scope.tabNames.forEachIndexed { page, text ->
                Tab(
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                    selected = pagerState.currentPage == page,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(page)
                        }
                    },
                    text = {
                        Text(
                            text().uppercase(),
                            maxLines = 1,
                        )
                    },
                )
            }
        }

        Surface(elevation = 4.dp) {
            if (fullWidthTabs) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = indicator,
                    tabs = tabs,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.Unspecified,
                )
            } else {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = indicator,
                    modifier = Modifier.fillMaxWidth(),
                    tabs = tabs,
                    backgroundColor = Color.Unspecified,
                    edgePadding = 0.dp,
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            beyondBoundsPageCount = beyondBoundsPageCount,
        ) { page ->
            scope.tabContents[page]()
        }
    }
}

interface TabPagerScope {
    fun tab(name: @Composable () -> String, content: @Composable () -> Unit)
}

private class TabCollector : TabPagerScope {
    private val _tabNames = mutableListOf<@Composable () -> String>()
    private val _tabContents = mutableListOf<@Composable () -> Unit>()

    val tabNames: List<@Composable () -> String> get() = _tabNames
    val tabContents: List<@Composable () -> Unit> get() = _tabContents

    override fun tab(name: @Composable () -> String, content: @Composable () -> Unit) {
        _tabNames += name
        _tabContents += content
    }
}

fun TabPagerScope.tab(name: String, content: @Composable () -> Unit) = tab(
    name = { name },
    content = content,
)

private fun Modifier.pagerTabIndicatorOffset(
    pagerState: androidx.compose.foundation.pager.PagerState,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier {
    return layout { measurable, constraints ->
        if (tabPositions.isEmpty()) {
            // If there are no pages, nothing to show
            return@layout layout(constraints.maxWidth, 0) {}
        }
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffsetFraction
        val indicatorWidth = when {
            fraction > 0 && nextTab != null -> {
                lerp(currentTab.width, nextTab.width, fraction).roundToPx()
            }

            fraction < 0 && previousTab != null -> lerp(
                currentTab.width,
                previousTab.width,
                -fraction
            ).roundToPx()

            else -> currentTab.width.roundToPx()
        }

        val indicatorOffset = when {
            fraction > 0 && nextTab != null -> {
                lerp(currentTab.left, nextTab.left, fraction).roundToPx()
            }

            fraction < 0 && previousTab != null -> {
                lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
            }

            else -> currentTab.left.roundToPx()
        }
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )

        return@layout layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
}
