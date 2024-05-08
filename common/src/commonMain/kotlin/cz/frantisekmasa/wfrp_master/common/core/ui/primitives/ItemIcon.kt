package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource

object ItemIcon {
    enum class Size {
        Small,
        Large,
        XLarge,
        ;

        val dimensions: Dp
            get() =
                when (this) {
                    Small -> 20.dp
                    Large -> 24.dp
                    XLarge -> 48.dp
                }

        val padding: Dp
            get() =
                when (this) {
                    Small -> 10.dp
                    Large -> 12.dp
                    XLarge -> 24.dp
                }
    }
}

@Composable
fun ItemIcon(
    url: String,
    size: ItemIcon.Size = ItemIcon.Size.Small,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)

    val dimensions = size.dimensions + size.padding * 2

    Image(
        rememberImagePainter(url).value,
        // TODO: Provide mechanism to specify what does this image means,
        //  such as: ("Character's image", "Strength-based skill", etc.)
        VISUAL_ONLY_ICON_DESCRIPTION,
        modifier =
            modifier
                .clip(CircleShape)
                .background(backgroundColor, CircleShape)
                .width(dimensions)
                .height(dimensions),
    )
}

@Composable
internal expect fun rememberImagePainter(url: String): State<Painter>

@Composable
fun ItemIcon(
    drawable: Resources.Drawable,
    size: ItemIcon.Size = ItemIcon.Size.Small,
    tint: Color = defaultTint(),
    backgroundColor: Color = defaultBackgroundColor(),
) {
    ItemIcon(drawableResource(drawable), size, backgroundColor, tint)
}

@Composable
fun ItemIcon(
    icon: ImageVector,
    size: ItemIcon.Size = ItemIcon.Size.Small,
    backgroundColor: Color = defaultBackgroundColor(),
    tint: Color = defaultTint(),
) {
    ItemIcon(rememberVectorPainter(icon), size, backgroundColor, tint)
}

@Composable
private fun ItemIcon(
    painter: Painter,
    size: ItemIcon.Size,
    backgroundColor: Color,
    tint: Color,
) {
    Image(
        painter,
        // TODO: Provide mechanism to specify what does this image means,
        //  such as: ("Character's image", "Strength-based skill", etc.)
        VISUAL_ONLY_ICON_DESCRIPTION,
        colorFilter = ColorFilter.tint(tint),
        modifier =
            Modifier
                .background(backgroundColor, CircleShape)
                .padding(size.padding)
                .width(size.dimensions)
                .height(size.dimensions),
    )
}

@Composable
fun defaultBackgroundColor() = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)

@Composable
private fun defaultTint(): Color = MaterialTheme.colors.surface
