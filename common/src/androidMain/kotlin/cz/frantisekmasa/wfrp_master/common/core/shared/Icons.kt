@file:JvmName("CommonIcons")

package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
actual fun platformDrawableResource(drawablePath: String): Painter {
    return painterResource(rememberDrawableId(drawablePath))
}

@Composable
private fun rememberDrawableId(drawablePath: String): Int {
    val context = LocalContext.current

    return remember {
        val resources = context.resources
        val imageName = drawablePath.substringAfterLast("/").substringBeforeLast(".")

        resources.getIdentifier(imageName, "drawable", context.packageName)
    }
}
