package cz.frantisekmasa.wfrp_master.core.ui.forms

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal object Colors {
    @Composable
    fun inputBorderColor(): Color = MaterialTheme.colors.onSurface.copy(alpha = 0.15f)
}
