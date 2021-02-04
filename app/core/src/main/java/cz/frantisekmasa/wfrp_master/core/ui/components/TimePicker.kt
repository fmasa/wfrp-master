package cz.frantisekmasa.wfrp_master.core.ui.components

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.frantisekmasa.wfrp_master.core.domain.time.DateTime
import kotlin.math.*

// TODO: Use original library when it supports current Compose Alpha 11
// https://github.com/vanpra/compose-material-dialogs

private data class SelectedOffset(
    val lineOffset: Offset = Offset.Zero,
    val selectedOffset: Offset = Offset.Zero
)

internal fun Float.getOffset(angle: Double): Offset =
    Offset((this * cos(angle)).toFloat(), (this * sin(angle)).toFloat())

@Composable
fun TimePickerLayout(
    modifier: Modifier = Modifier,
    selectedTime: MutableState<DateTime.TimeOfDay>
) {
    val currentScreen = remember { mutableStateOf(0) }
    Box(modifier) {
        WithConstraints {
            Column(
                Modifier
                    .heightIn(max = maxHeight * 0.8f)
                    .verticalScroll(rememberScrollState()),
            ) {
                TimeLayout(currentScreen, selectedTime)
                Crossfade(currentScreen) {
                    when (it.value) {
                        0 ->
                            ClockLayout(
                                isHours = true,
                                anchorPoints = 12,
                                label = { index ->
                                    if (index == 0) {
                                        "12"
                                    } else {
                                        index.toString()
                                    }
                                },
                                onAnchorChange = { hours ->
                                    selectedTime.value = selectedTime.value.copy(hour = hours)
                                },
                                selectedTime = selectedTime
                            ) {
                                currentScreen.value = 1
                            }

                        1 -> ClockLayout(
                            isHours = false,
                            anchorPoints = 60,
                            label = { index ->
                                index.toString().padStart(2, '0')
                            },
                            onAnchorChange = { mins ->
                                selectedTime.value = selectedTime.value.copy(minute = mins)
                            },
                            selectedTime = selectedTime
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeLayout(currentScreen: MutableState<Int>, selectedTime: MutableState<DateTime.TimeOfDay>) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primaryVariant)
    ) {
        val textSize = 60.sp
        val color = MaterialTheme.colors.onPrimary
        val hourAlpha = 1f - 0.4f * currentScreen.value
        val minAlpha = 0.6f + 0.4f * currentScreen.value

        Row(Modifier.align(Alignment.Center)) {
            Text(
                selectedTime.value.hour.toString().padStart(2, '0'),
                fontSize = textSize,
                color = color.copy(hourAlpha),
                modifier = Modifier.clickable(
                    onClick = { currentScreen.value = 0 },
                    interactionState = remember { InteractionState() },
                    indication = null
                )
            )

            Text(":", fontSize = textSize, color = color)

            Text(
                selectedTime.value.minute.toString().padStart(2, '0'),
                fontSize = textSize,
                color = color.copy(minAlpha),
                modifier = Modifier.clickable(
                    onClick = { currentScreen.value = 1 },
                    interactionState = remember { InteractionState() },
                    indication = null
                )
            )
        }
    }
}

@Composable
private fun ClockLayout(
    isHours: Boolean,
    anchorPoints: Int,
    label: (Int) -> String,
    selectedTime: MutableState<DateTime.TimeOfDay>,
    onAnchorChange: (Int) -> Unit = {},
    onLift: () -> Unit = {}
) {
    val outerRadius = with(AmbientDensity.current) { 100.dp.toPx() }
    val innerRadius = with(AmbientDensity.current) { 60.dp.toPx() }
    val selectedRadius = 70f

    val offset = remember { mutableStateOf(Offset.Zero) }
    val center = remember { mutableStateOf(Offset.Zero) }
    val namedAnchor = remember { mutableStateOf(true) }

    val anchors = remember {
        val anchors = mutableListOf<SelectedOffset>()
        for (x in 0 until anchorPoints) {
            val angle = (2 * PI / anchorPoints) * (x - 15)
            val selectedOuterOffset = outerRadius.getOffset(angle)
            val lineOuterOffset = (outerRadius - selectedRadius).getOffset(angle)

            anchors.add(
                SelectedOffset(
                    lineOuterOffset,
                    selectedOuterOffset
                )
            )

            if (isHours) {
                val selectedInnerOffset = innerRadius.getOffset(angle)
                val lineInnerOffset = (innerRadius - selectedRadius).getOffset(angle)
                anchors.add(
                    SelectedOffset(
                        lineInnerOffset,
                        selectedInnerOffset
                    )
                )
            }
        }
        anchors
    }

    val anchoredOffset = remember {
        mutableStateOf(
            if (!isHours) {
                namedAnchor.value = selectedTime.value.minute % 5 == 0
                anchors[selectedTime.value.minute]
            } else {
                when (selectedTime.value.hour) {
                    0 -> anchors[1]
                    in 1..11 -> anchors[selectedTime.value.hour * 2]
                    in 13..23 -> anchors[(selectedTime.value.hour - 12) * 2 + 1]
                    else -> anchors[0]
                }
            }
        )
    }

    fun updateAnchor() {
        val absDiff =
            anchors.map {
                val diff = it.selectedOffset - offset.value + center.value
                diff.x.pow(2) + diff.y.pow(2)
            }
        val minAnchor = absDiff.withIndex().minByOrNull { (_, f) -> f }?.index
        if (anchoredOffset.value.selectedOffset != anchors[minAnchor!!].selectedOffset) {
            onAnchorChange(
                if (isHours && minAnchor % 2 == 1) {
                    if (minAnchor != 1) {
                        (minAnchor / 2 + 12)
                    } else {
                        0
                    }
                } else if (isHours) {
                    label(minAnchor / 2).toInt()
                } else {
                    label(minAnchor).toInt()
                }
            )

            anchoredOffset.value = anchors[minAnchor]
            if (!isHours) {
                namedAnchor.value = minAnchor % 5 == 0
            }
        }
    }

    val dragObserver =
        object : DragObserver {
            override fun onStart(downPosition: Offset) {
                offset.value = Offset(downPosition.x, downPosition.y)
            }

            override fun onStop(velocity: Offset) {
                super.onStop(velocity)
                onLift()
            }

            override fun onDrag(dragDistance: Offset): Offset {
                offset.value = Offset(
                    offset.value.x + dragDistance.x,
                    offset.value.y + dragDistance.y
                )
                updateAnchor()
                return dragDistance
            }
        }

    val touchFilter = { pos: Offset ->
        offset.value = pos
        updateAnchor()
    }

    BoxWithConstraints(Modifier) {
        val constraints = constraints

        Box(
            Modifier
                .preferredSize(maxWidth)
                .pressIndicatorGestureFilter(touchFilter, onLift)
                .dragGestureFilter(dragObserver)
        ) {
            SideEffect {
                center.value = Offset(constraints.maxWidth / 2f, constraints.maxWidth / 2f)
                offset.value = center.value
            }

            val textColor = MaterialTheme.colors.onBackground.toArgb()
            val selectedColor = MaterialTheme.colors.secondary

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(selectedColor, radius = 16f)

                drawLine(
                    color = selectedColor,
                    start = center.value,
                    end = center.value + anchoredOffset.value.lineOffset,
                    strokeWidth = 10f,
                    alpha = 0.8f
                )

                drawCircle(
                    selectedColor,
                    center = center.value + anchoredOffset.value.selectedOffset,
                    radius = selectedRadius,
                    alpha = 0.7f
                )

                if (!namedAnchor.value) {
                    drawCircle(
                        Color.White,
                        center = center.value + anchoredOffset.value.selectedOffset,
                        radius = 10f,
                        alpha = 0.8f
                    )
                }

                drawIntoCanvas { canvas ->
                    for (x in 0 until 12) {
                        val angle = (2 * PI / 12) * (x - 15)
                        val textOuter = label(x * anchorPoints / 12)

                        drawText(
                            60f,
                            textOuter,
                            center.value,
                            angle.toFloat(),
                            canvas,
                            outerRadius,
                            color = textColor
                        )

                        if (isHours) {
                            val textInner = if (x != 0) {
                                (x + 12).toString()
                            } else {
                                "00"
                            }

                            drawText(
                                45f,
                                textInner,
                                center.value,
                                angle.toFloat(),
                                canvas,
                                innerRadius,
                                200,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun drawText(
    textSize: Float,
    text: String,
    center: Offset,
    angle: Float,
    canvas: Canvas,
    radius: Float,
    alpha: Int = 255,
    color: Int = android.graphics.Color.WHITE
) {
    val outerText = Paint()
    outerText.color = color
    outerText.textSize = textSize
    outerText.textAlign = Paint.Align.CENTER
    outerText.alpha = alpha

    val r = Rect()
    outerText.getTextBounds(text, 0, text.length, r)

    canvas.nativeCanvas.drawText(
        text,
        center.x + (radius * cos(angle)),
        center.y + (radius * sin(angle)) + (abs(r.height())) / 2,
        outerText
    )
}