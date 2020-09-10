package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.TabPosition
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed


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