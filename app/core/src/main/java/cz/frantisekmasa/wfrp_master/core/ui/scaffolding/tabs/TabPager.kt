package cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Pager
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.rememberPagerState

@Composable
fun TabPager(
    modifier: Modifier = Modifier,
    fullWidthTabs: Boolean = false,
    tabs: TabPagerScope.() -> Unit
) {
    BoxWithConstraints(modifier) {
        val scope = TabCollector(maxWidth)
        scope.apply(tabs)

        val pagerState = rememberPagerState(
            screenWidth = constraints.maxWidth,
            screenCount = scope.tabContents.size,
        )

        Column {
            TabRow(
                names = scope.tabNames.map { it() },
                pagerState = pagerState,
                fullWidthTabs = fullWidthTabs,
            )

            Pager(
                state = pagerState,
                screens = scope.tabContents,
            )
        }
    }

}

interface TabPagerScope {
    val screenWidth: Dp

    fun tab(name: @Composable () -> String, content: @Composable () -> Unit)
}

private class TabCollector(override val screenWidth: Dp) : TabPagerScope {
    private val _tabNames = mutableListOf<@Composable () -> String>()
    private val _tabContents = mutableListOf<@Composable () -> Unit>()

    val tabNames: List<@Composable () -> String> get() = _tabNames
    val tabContents: List<@Composable () -> Unit> get() = _tabContents

    override fun tab(name: @Composable () -> String, content: @Composable () -> Unit) {
        _tabNames += name
        _tabContents += content
    }
}

fun TabPagerScope.tab(@StringRes name: Int, content: @Composable () -> Unit) = tab(
    name = { stringResource(name) },
    content = content,
)
