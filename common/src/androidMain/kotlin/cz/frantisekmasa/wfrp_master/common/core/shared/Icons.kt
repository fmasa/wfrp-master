@file:JvmName("CommonIcons")

package cz.frantisekmasa.wfrp_master.common.core.shared


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import cz.frantisekmasa.wfrp_master.common.R

@Composable
actual fun platformDrawableResource(drawablePath: String): Painter {
    return painterResource(rememberDrawableId(drawablePath))
}

@Composable
private fun rememberDrawableId(drawablePath: String): Int = remember {
    val imageName = drawablePath.substringAfterLast("/").substringBeforeLast(".")
    val drawableClass = R.drawable::class.java
    val field = drawableClass.getDeclaredField(imageName)

    field.get(drawableClass) as Int
}