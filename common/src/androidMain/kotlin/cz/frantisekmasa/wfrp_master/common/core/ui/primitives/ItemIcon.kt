@file:JvmName("CommonItemIcon")

package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation


@Composable
internal actual fun rememberCircleImagePainter(url: String): Painter {
    return rememberImagePainter(url) {
        transformations(CircleCropTransformation())
    }
}
