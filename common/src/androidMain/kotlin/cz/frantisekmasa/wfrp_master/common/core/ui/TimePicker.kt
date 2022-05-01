package cz.frantisekmasa.wfrp_master.common.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.title
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import java.time.LocalTime


@Composable
actual fun timePicker(time: LocalTime, onTimeChange: TimePicker.(LocalTime) -> Unit): TimePicker {
    val dialog = remember { MaterialDialog() }
    val timePicker = VanpraTimePicker(dialog)

    dialog.build(
        buttons = {
            positiveButton(LocalStrings.current.commonUi.buttonSave)
            negativeButton(LocalStrings.current.commonUi.buttonCancel)
        }
    ) {
        title(LocalStrings.current.calendar.titleSelectTime)
        timepicker(time, onTimeChange = { timePicker.onTimeChange(it) })
    }

    return timePicker
}

class VanpraTimePicker(private val dialog: MaterialDialog): TimePicker {
    override fun show() {
        dialog.show()
    }

    override fun hide() {
        dialog.hide()
    }
}
