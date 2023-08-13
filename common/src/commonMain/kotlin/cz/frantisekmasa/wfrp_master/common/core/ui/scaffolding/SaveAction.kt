package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SaveAction(onClick: () -> Unit, enabled: Boolean = true) {
    TopBarAction(
        text = stringResource(Str.common_ui_button_save).uppercase(),
        enabled = enabled,
        onClick = onClick,
    )
}
