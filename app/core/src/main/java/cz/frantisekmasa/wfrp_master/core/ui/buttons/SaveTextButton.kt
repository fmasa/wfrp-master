package cz.frantisekmasa.wfrp_master.core.ui.buttons

import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.core.ui.texts.SaveButtonText

@Composable
fun SaveTextButton(onClick: () -> Unit, enabled: Boolean = true) {
    TextButton(onClick = onClick, enabled = enabled) { SaveButtonText() }
}