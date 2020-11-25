package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ItemIcon {
    enum class Size {
        Small,
        Large;

        val dimensions: Dp
            get() = when (this) {
                Small -> 20.dp
                Large -> 24.dp
            }

        val padding: Dp
            get() = when (this) {
                Small -> 10.dp
                Large -> 12.dp
            }
    }
}

@Composable
fun ItemIcon(@DrawableRes drawableResource: Int, size: ItemIcon.Size = ItemIcon.Size.Small) {
    val backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)

    Image(
        vectorResource(drawableResource),
        colorFilter = ColorFilter.tint(MaterialTheme.colors.surface),
        modifier = Modifier
            .background(backgroundColor, CircleShape)
            .padding(size.padding)
            .width(size.dimensions)
            .height(size.dimensions)
    )
}
