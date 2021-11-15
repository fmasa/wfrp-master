package cz.frantisekmasa.wfrp_master.common.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier

val LocalScreenWidth = compositionLocalOf<Dp> { error("Wrap your screen by ScreenWithBreakpoints") }
val LocalBreakpoint = staticCompositionLocalOf<Breakpoint> { error("Wrap your screen by ScreenWithBreakpoints") }
private const val GRID_COLUMNS = 12

@Composable
fun ScreenWithBreakpoints(content: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val breakpoint = resolveBreakpoint(maxWidth)

        Napier.d("Breakpoint calculated as ${breakpoint.name}, width: $maxWidth")

        CompositionLocalProvider(
            LocalBreakpoint provides breakpoint,
            LocalScreenWidth provides maxWidth,
            content = content,
        )
    }
}

@Composable
@Stable
fun resolveBreakpoint(width: Dp): Breakpoint {
    return remember(width) {
        Breakpoint
            .values()
            .asSequence()
            .takeWhile { width >= it.minWidth }
            .last()
    }
}

@Composable
fun Container(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: ContainerScope.() -> Unit,
) {
    Column(modifier, verticalArrangement = verticalArrangement) {
        val scope by rememberStateOfColumns(content)

        scope.rows.forEach { row ->
            Row(horizontalArrangement = horizontalArrangement) {
                row.forEach { column ->
                    Column(Modifier.weight(column.weight.toFloat()), content = column.content)
                }
            }
        }
    }
}

interface ContainerScope {
    val breakpoint: Breakpoint

    fun column(
        size: ColumnSize = ColumnSize.FullWidth,
        content: @Composable ColumnScope.() -> Unit
    )
}

@Composable
private fun rememberStateOfColumns(content: ContainerScope.() -> Unit): State<ContainerScopeImpl> {
    val latestContent = rememberUpdatedState(content)
    val breakpoint = LocalBreakpoint.current

    return remember {
        derivedStateOf { ContainerScopeImpl(breakpoint).apply(latestContent.value) }
    }
}

enum class ColumnSize(val value: Int) {
    FullWidth(GRID_COLUMNS),
    HalfWidth(FullWidth.value / 2),
}

private class ContainerScopeImpl(override val breakpoint: Breakpoint) : ContainerScope {
    private val columns = mutableListOf<Column>()

    val rows: List<List<Column>> get() {
        val rows = mutableListOf<List<Column>>()
        var currentRow = mutableListOf<Column>()
        var weightSum = 0

        for (column in columns) {
            val weight = column.weight

            if (weightSum + weight > GRID_COLUMNS) {
                // End row
                rows += currentRow
                currentRow = mutableListOf()
                weightSum = 0
            }

            weightSum += weight
            currentRow += column
        }

        rows += currentRow

        return rows
    }

    override fun column(
        size: ColumnSize,
        content: @Composable ColumnScope.() -> Unit
    ) {
        columns += Column(size.value, content)
    }

    data class Column(
        val weight: Int,
        val content: @Composable ColumnScope.() -> Unit,
    )
}

enum class Breakpoint(val minWidth: Dp) {
    XSmall(0.dp),
    Small(480.dp),
    Medium(840.dp),
    Large(1280.dp),
    XLarge(1440.dp)
}
