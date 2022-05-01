package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
actual fun platformDrawableResource(drawablePath: String): Painter {
    return painterResource(drawablePath)
}
