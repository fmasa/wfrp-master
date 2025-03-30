package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.LocalScreenHeight
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.LocalScreenWidth

@Composable
actual fun FullScreenDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        DisableScrim()

        // On Pixel, the insets in dialog do not work properly, so we are just limiting the dialog
        // size to the screen size
        // See https://github.com/fmasa/wfrp-master/issues/429
        Box(
            Modifier.width(LocalScreenWidth.current)
                .height(LocalScreenHeight.current),
        ) {
            content()
        }
    }
}

@Composable
fun DisableScrim() {
    val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
    SideEffect {
        dialogWindow?.setDimAmount(0f)
    }
}
