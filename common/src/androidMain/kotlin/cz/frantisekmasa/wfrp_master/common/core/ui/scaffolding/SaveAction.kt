package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun SaveAction(onClick: () -> Unit, enabled: Boolean = true) {
    TopBarAction(
        text = LocalStrings.current.commonUi.buttonSave,
        enabled = enabled,
        onClick = onClick,
    )
}
