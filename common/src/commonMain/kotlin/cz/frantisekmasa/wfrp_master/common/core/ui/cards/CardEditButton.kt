package cz.frantisekmasa.wfrp_master.common.core.ui.cards

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun CardEditButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Rounded.Edit,
            LocalStrings.current.commonUi.buttonEdit,
            tint = MaterialTheme.colors.primary,
        )
    }
}
