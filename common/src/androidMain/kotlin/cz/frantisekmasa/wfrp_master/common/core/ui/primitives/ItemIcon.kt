@file:JvmName("CommonItemIcon")

package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
internal actual fun rememberImagePainter(url: String): State<Painter> {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(url)
            .build()
    )

    return derivedStateOf { painter }
}
