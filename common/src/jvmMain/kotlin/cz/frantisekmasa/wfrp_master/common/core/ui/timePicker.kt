package cz.frantisekmasa.wfrp_master.common.core.ui

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.PersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource
import java.time.LocalTime

@Composable
actual fun timePicker(
    time: LocalTime,
    onTimeChange: TimePicker.(LocalTime) -> Unit
): TimePicker {
    val snackbarHolder = LocalPersistentSnackbarHolder.current
    val errorMessage = stringResource(Str.messages_non_desktop_feature)

    return remember(snackbarHolder, errorMessage) {
        // TODO: Waiting for upstream https://github.com/vanpra/compose-material-dialogs/issues/43
        DummyTimePicker(snackbarHolder, errorMessage)
    }
}

class DummyTimePicker(
    private val snackbarHolder: PersistentSnackbarHolder,
    private val errorMessage: String,
) : TimePicker {
    override fun show() {
        snackbarHolder.showSnackbar(errorMessage, SnackbarDuration.Long)
    }

    override fun hide() {
    }
}
