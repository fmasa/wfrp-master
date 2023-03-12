package cz.frantisekmasa.wfrp_master.common.core.ui.buttons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun CloseButton(
    onClick: () -> Unit,
    contentDescription: String = LocalStrings.current.commonUi.labelCloseDialog,
) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Rounded.Close,
            contentDescription = contentDescription,
        )
    }
}
