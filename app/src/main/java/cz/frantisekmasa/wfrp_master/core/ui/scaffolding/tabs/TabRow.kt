package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.PagerState

@Composable
internal fun ColumnScope.TabRow(
    names: List<String>,
    pagerState: PagerState,
    fullWidthTabs: Boolean = false
) {
    val selectedTabIndex = pagerState.selectedScreen
    val scrolledPercentage = pagerState.scrolledPercentage

    val tabs = @Composable {
        val tabModifier = if (fullWidthTabs) Modifier.weight(1f) else Modifier

        names.forEachIndexed { index, tabName ->
            Tab(
                modifier = tabModifier,
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                selected = index == (if (scrolledPercentage > 0.5f) selectedTabIndex + 1 else selectedTabIndex),
                onClick = { pagerState.selectScreen(index) },
                text = {
                    Text(
                        tabName.uppercase(),
                        maxLines = 1,
                    )
                },
            )
        }
    }

    val animatedSelectedTabIndex =
        if (scrolledPercentage > 0.5f) selectedTabIndex + 1 else selectedTabIndex

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        Box(
            Modifier
                .animatedTabIndicatorOffset(
                    tabPositions,
                    selectedTabIndex,
                    scrolledPercentage
                )
                .fillMaxWidth()
                /* TODO: REMOVE COMMENT */
                .height(2.dp)
                .background(color = MaterialTheme.colors.primary)
        )
    }

    Surface(elevation = 4.dp) {
        val modifier = Modifier.fillMaxWidth()

        if (fullWidthTabs) {
            TabRow(
                modifier = modifier,
                selectedTabIndex = animatedSelectedTabIndex,
                backgroundColor = Color.Unspecified,
                indicator = indicator,
                tabs = tabs,
            )
        } else {
            ScrollableTabRow(
                edgePadding = 0.dp,
                modifier = modifier,
                selectedTabIndex = animatedSelectedTabIndex,
                backgroundColor = Color.Unspecified,
                indicator = indicator,
                tabs = tabs,
            )
        }
    }
}

private fun Modifier.animatedTabIndicatorOffset(
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
        /* TODO: REMOVE COMMENT */
        .width(currentTabWidth + widthDiff * scrolledPercentage)
}
