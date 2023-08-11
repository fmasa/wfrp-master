package cz.frantisekmasa.wfrp_master.common.core.ui.buttons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.config.Platform
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun HamburgerButton() {
    if (
        LocalStaticConfiguration.current.platform == Platform.Desktop &&
        LocalNavigationTransaction.current.canPop
    ) {
        BackButton()
        return
    }

    val callback = LocalHamburgerButtonHandler.current
    IconButton(onClick = callback) {
        Icon(
            Icons.Rounded.Menu,
            stringResource(Str.common_ui_label_open_drawer),
        )
    }
}
