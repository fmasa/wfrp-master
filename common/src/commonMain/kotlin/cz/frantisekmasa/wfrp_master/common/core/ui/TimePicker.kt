package cz.frantisekmasa.wfrp_master.common.core.ui

import androidx.compose.runtime.Composable
import java.time.LocalTime

@Composable
expect fun timePicker(time: LocalTime, onTimeChange: TimePicker.(LocalTime) -> Unit): TimePicker

interface TimePicker {
    fun show()
    fun hide()
}

