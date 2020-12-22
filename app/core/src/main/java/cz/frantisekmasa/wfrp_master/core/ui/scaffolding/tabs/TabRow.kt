package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.*


@Composable
fun <T> ColumnScope.TabRow(
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
                unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
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
            color = MaterialTheme.colors.primary,
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
        .preferredWidth(currentTabWidth + widthDiff * scrolledPercentage)
}