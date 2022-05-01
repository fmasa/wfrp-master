@file:JvmName("CommonItemIcon")

package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation


@Composable
internal actual fun rememberImagePainter(url: String): State<Painter> {
    val painter = rememberImagePainter(url) {
        transformations(CircleCropTransformation())
    }

    return derivedStateOf { painter }
}
