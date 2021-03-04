package cz.muni.fi.rpg.ui.shell

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
@ExperimentalMaterialApi
fun ModalDrawerLayoutWithFixedDrawerWidth(
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colors.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    scrimColor: Color = DrawerDefaults.scrimColor,
    bodyContent: @Composable () -> Unit
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        // TODO : think about Infinite max bounds case
        if (!constraints.hasBoundedWidth) {
            throw IllegalStateException("Drawer shouldn't have infinite width")
        }

        val coroutineScope = rememberCoroutineScope()
        val mainConstraints = constraints
        val minValue = -mainConstraints.maxWidth.toFloat()
        val maxValue = 0f

        val anchors = mapOf(minValue to DrawerValue.Closed, maxValue to DrawerValue.Open)
        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        Box(
            Modifier.swipeable(
                state = drawerState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                enabled = gesturesEnabled,
                reverseDirection = isRtl,
                velocityThreshold = DrawerVelocityThreshold,
                resistance = null
            )
        ) {
            Box {
                bodyContent()
            }
            Scrim(
                open = drawerState.isOpen,
                onClose = { coroutineScope.launch { drawerState.close() } },
                fraction = { calculateFraction(minValue, maxValue, drawerState.offset.value) },
                color = scrimColor
            )

            Surface(
                modifier = with(LocalDensity.current) {
                    Modifier.sizeIn( /* TODO: REMOVE COMMENT */
                        minWidth = DrawerWidth,
                        minHeight = mainConstraints.minHeight.toDp(),
                        maxWidth = DrawerWidth,
                        maxHeight = mainConstraints.maxHeight.toDp()
                    )
                }
                    .semantics {
                        if (drawerState.isOpen) {
                            dismiss(action = {
                                coroutineScope.launch { drawerState.close() };
                                true
                            })
                        }
                    }
                    .offset { IntOffset(drawerState.offset.value.roundToInt(), 0) },
                shape = drawerShape,
                color = drawerBackgroundColor,
                contentColor = drawerContentColor,
                elevation = drawerElevation
            ) {
                Column(Modifier.fillMaxSize(), content = drawerContent)
            }
        }
    }
}

@Composable
private fun Scrim(
    open: Boolean,
    onClose: () -> Unit,
    fraction: () -> Float,
    color: Color
) {
    val dismissDrawer = if (open) {
        Modifier.pointerInput(onClose) {
            detectTapGestures { onClose() }
        }
    } else {
        Modifier
    }

    Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissDrawer)
    ) {
        drawRect(color, alpha = fraction())
    }
}

private fun calculateFraction(a: Float, b: Float, pos: Float) =
    ((pos - a) / (b - a)).coerceIn(0f, 1f)

private val DrawerVelocityThreshold = 400.dp
private val DrawerWidth = 280.dp

