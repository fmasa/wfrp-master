package cz.frantisekmasa.wfrp_master.common.core.ui.buttons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun HamburgerButton() {
    val callback = LocalHamburgerButtonHandler.current
    IconButton(onClick = callback) {
        Icon(
            Icons.Rounded.Menu,
            LocalStrings.current.commonUi.labelOpenDrawer,
        )
    }
}
