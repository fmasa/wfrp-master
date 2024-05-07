package cz.frantisekmasa.wfrp_master.common.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.title
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.resources.compose.stringResource
import java.time.LocalTime

@Composable
actual fun timePicker(
    time: LocalTime,
    onTimeChange: TimePicker.(LocalTime) -> Unit,
): TimePicker {
    val dialog = remember { MaterialDialog() }
    val timePicker = VanpraTimePicker(dialog)

    dialog.build(
        buttons = {
            positiveButton(stringResource(Str.common_ui_button_save))
            negativeButton(stringResource(Str.common_ui_button_cancel))
        },
    ) {
        title(stringResource(Str.calendar_title_select_time))
        timepicker(time, onTimeChange = { timePicker.onTimeChange(it) })
    }

    return timePicker
}

class VanpraTimePicker(private val dialog: MaterialDialog) : TimePicker {
    override fun show() {
        dialog.show()
    }

    override fun hide() {
        dialog.hide()
    }
}
