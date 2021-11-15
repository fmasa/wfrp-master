package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun vectorResource(drawable: Resources.Drawable): Painter = platformVectorResource(drawable.path)

@Composable
expect fun platformVectorResource(drawablePath: String): Painter