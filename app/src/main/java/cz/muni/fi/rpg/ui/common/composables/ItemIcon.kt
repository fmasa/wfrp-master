package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R

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
    val backgroundColor = colorResource(R.color.colorPrimaryDark)

    Image(
        vectorResource(drawableResource),
        colorFilter = ColorFilter.tint(colorResource(R.color.colorFabText)),
        modifier = Modifier
            .background(backgroundColor, CircleShape)
            .padding(size.padding)
            .width(size.dimensions)
            .height(size.dimensions)
    )
}
