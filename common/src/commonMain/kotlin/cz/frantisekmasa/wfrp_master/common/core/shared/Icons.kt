package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun drawableResource(drawable: Resources.Drawable): Painter = platformDrawableResource(drawable.path)

@Composable
expect fun platformDrawableResource(drawablePath: String): Painter
