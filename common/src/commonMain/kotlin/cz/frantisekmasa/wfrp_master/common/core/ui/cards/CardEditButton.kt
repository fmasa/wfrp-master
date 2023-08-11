package cz.frantisekmasa.wfrp_master.common.core.ui.cards

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CardEditButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Rounded.Edit,
            stringResource(MR.strings.common_ui_button_edit),
            tint = MaterialTheme.colors.primary,
        )
    }
}
