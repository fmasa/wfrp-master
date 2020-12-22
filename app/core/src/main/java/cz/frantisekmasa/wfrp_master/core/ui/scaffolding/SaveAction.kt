package cz.frantisekmasa.wfrp_master.core.ui.scaffolding

import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.core.ui.texts.SaveButtonText

@Composable
fun SaveAction(onClick: () -> Unit, enabled: Boolean = true) {
    TopBarAction(enabled = enabled, onClick = onClick) {
        SaveButtonText()
    }
}
