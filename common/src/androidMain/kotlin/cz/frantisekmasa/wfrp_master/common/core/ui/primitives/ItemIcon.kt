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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource

object ItemIcon {
    enum class Size {
        Small,
        Large,
        XLarge;

        val dimensions: Dp
            get() = when (this) {
                Small -> 20.dp
                Large -> 24.dp
                XLarge -> 48.dp
            }

        val padding: Dp
            get() = when (this) {
                Small -> 10.dp
                Large -> 12.dp
                XLarge -> 24.dp
            }
    }
}

@Composable
fun ItemIcon(url: String, size: ItemIcon.Size = ItemIcon.Size.Small) {
    val backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)

    val dimensions = size.dimensions + size.padding * 2

    Image(
        rememberImagePainter(url) {
            transformations(CircleCropTransformation())
        },
        VisualOnlyIconDescription, // TODO: Provide mechanism to specify what does this image means, such as: ("Character's image", "Strength-based skill", etc.)
        modifier = Modifier
            .background(backgroundColor, CircleShape)
            .width(dimensions)
            .height(dimensions)
    )
}

@Composable
fun ItemIcon(drawable: Resources.Drawable, size: ItemIcon.Size = ItemIcon.Size.Small) {
    ItemIcon(drawableResource(drawable), size)
}

@Composable
fun ItemIcon(icon: ImageVector, size: ItemIcon.Size = ItemIcon.Size.Small) {
    ItemIcon(rememberVectorPainter(icon), size)
}

@Composable
private fun ItemIcon(painter: Painter, size: ItemIcon.Size) {
    val backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)

    Image(
        painter,
        VisualOnlyIconDescription, // TODO: Provide mechanism to specify what does this image means, such as: ("Character's image", "Strength-based skill", etc.)
        colorFilter = ColorFilter.tint(MaterialTheme.colors.surface),
        modifier = Modifier
            .background(backgroundColor, CircleShape)
            .padding(size.padding)
            .width(size.dimensions)
            .height(size.dimensions)
    )
}


