package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

object EmptyUI {
    enum class Size {

        /**
         * Should be used, when there are more lists on a screen and this empty UI is only related
         * to part of it
         */
        Small,

        /**
         * Should be used when main content of screen is empty
         */
        Large;

        val modifier: Modifier
            get() = when (this) {
                Small -> Modifier.width(60.dp).padding(top = 16.dp)
                Large -> Modifier.fillMaxHeight(0.35f)
            }

        @Composable
        val textStyle: TextStyle get() = when (this) {
            Small -> MaterialTheme.typography.subtitle1
            Large -> MaterialTheme.typography.h6
        }
    }
}

@Composable
fun EmptyUI(
    @StringRes textId: Int,
    @DrawableRes drawableResourceId: Int,
    size: EmptyUI.Size = EmptyUI.Size.Large
) {
    val image = vectorResource(drawableResourceId)
    val color = EmphasisAmbient.current.medium.applyEmphasis(MaterialTheme.colors.onSurface)
    val text = stringResource(textId)

    Column(horizontalGravity = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.fillMaxHeight(0.35f))
        Image(image, modifier = size.modifier, colorFilter = ColorFilter.tint(color))

        Text(
            text,
            style = size.textStyle,
            color = color,
        )
    }
}